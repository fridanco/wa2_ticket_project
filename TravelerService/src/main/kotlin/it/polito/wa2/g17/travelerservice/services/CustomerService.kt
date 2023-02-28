package it.polito.wa2.g17.travelerservice.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.travelerservice.dtos.TicketPurchasedDto
import it.polito.wa2.g17.travelerservice.dtos.UserProfileDto
import it.polito.wa2.g17.travelerservice.dtos.toDTO
import it.polito.wa2.g17.travelerservice.entities.TicketPurchased
import it.polito.wa2.g17.travelerservice.entities.UserDetails
import it.polito.wa2.g17.travelerservice.exceptions.TicketNotFoundException
import it.polito.wa2.g17.travelerservice.exceptions.UserEmptyProfileException
import it.polito.wa2.g17.travelerservice.kafka.entities.received.KafkaTicketRequestTravelerServiceRequest
import it.polito.wa2.g17.travelerservice.kafka.entities.sent.KafkaTicketRequestTravelerServiceResponse
import it.polito.wa2.g17.travelerservice.repositories.TicketPurchasedRepository
import it.polito.wa2.g17.travelerservice.repositories.UserDetailsRepository
import it.polito.wa2.g17.travelerservice.utils.PDFGenerator
import it.polito.wa2.g17.travelerservice.utils.QRCodeGenerator
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.security.Key
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class CustomerService : InitializingBean {

    @Autowired
    private lateinit var qrCodeGenerator: QRCodeGenerator

    @Autowired
    private lateinit var pdfGenerator: PDFGenerator

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Qualifier("topic_travelerServiceToTicketCatalogueService")
    @Autowired
    lateinit var travelerServiceToTicketCatalogueService: NewTopic

    @Value("\${ticketValidationJwtSecret}")
    lateinit var secret : String

    lateinit var hmacKey: Key

    override fun afterPropertiesSet() {
        hmacKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)
    }

    fun getProfile(authenticationUsername: String) : UserDetails{

        val profile = userDetailsRepository.findByIdOrNull(authenticationUsername) ?: throw UserEmptyProfileException()

        println("Profile found")

        return profile
    }

    fun updateProfile(authenticationUsername: String, userProfileDto: UserProfileDto){

        val profile = userDetailsRepository.findByIdOrNull(authenticationUsername)
        if(profile==null){
            userDetailsRepository.save(UserDetails(authenticationUsername, userProfileDto.name, userProfileDto.address, userProfileDto.dateOfBirth, userProfileDto.telephoneNumber, mutableListOf()))
        }
        else{
            profile.apply {
                this.name = userProfileDto.name
                this.address = userProfileDto.address
                this.dateOfBirth = userProfileDto.dateOfBirth
                this.telephoneNumber = userProfileDto.telephoneNumber
            }
            userDetailsRepository.save(profile)
        }
    }

    fun getTickets(authenticationUsername: String) : List<TicketPurchasedDto> {
        val profile = userDetailsRepository.findByIdOrNull(authenticationUsername) ?: return listOf()
        val tickets = ticketPurchasedRepository.findAllByUserDetails(profile)
        return tickets.map { it.toDTO() }
    }

    fun getTicketsByOrderID(authenticationUsername: String, orderID: UUID): List<TicketPurchasedDto> {
        val profile = userDetailsRepository.findByIdOrNull(authenticationUsername) ?: return listOf()
        val tickets = ticketPurchasedRepository.findAllByUserDetailsAndOrderId(profile, orderID)
        return tickets.map { it.toDTO() }
    }


    fun getTicketQRByTicketID(authenticationUsername: String, ticketID: UUID): ResponseEntity<InputStreamResource> {
        val profile = userDetailsRepository.findByIdOrNull(authenticationUsername) ?: throw UserEmptyProfileException()
        val ticket = ticketPurchasedRepository.findByUserDetailsAndId(profile, ticketID) ?: throw TicketNotFoundException(ticketID)

        val imgData : ByteArray = qrCodeGenerator.getQRCode(ticket.jws, 0, 0)
        val inStreamRes : InputStreamResource = pdfGenerator.generatePDF(
            profile.name,
            ticket.id,
            ticket.orderId,
            ticket.ticketType,
            Date(ticket.iat),
            Date(ticket.exp),
            ticket.validFrom,
            imgData
        )

        val headers = HttpHeaders()
        headers.add("Content-Disposition", "inline; "+"filename=wa2g17-ticket-${ticket.id}.pdf")

        return ResponseEntity.ok().headers(headers)
            .contentType(MediaType.APPLICATION_PDF)
            .body(inStreamRes)
    }


    @KafkaListener(topics = ["ticketCatalogueService_travelerService"], groupId = "it.polito.wa2.travelerservice")
    fun requireTickets(message: String){

        var ticketOrderRequest: KafkaTicketRequestTravelerServiceRequest? = null

        try {
            val mapper = jacksonObjectMapper()
            ticketOrderRequest = mapper.readValue(message, KafkaTicketRequestTravelerServiceRequest::class.java)
        }
        catch (e: Exception) {
            println(e.message)
            return
        }

        try{
            if (ticketOrderRequest.cmd == "buy_tickets") {
                val profile = userDetailsRepository.findByIdOrNull(ticketOrderRequest.username)
                if (profile == null) {
                    sendMessageKafkaToTicketCatalogueService(
                        KafkaTicketRequestTravelerServiceResponse(
                            ticketOrderRequest.orderId,
                            "buy_tickets",
                            false,
                            Date()
                        )
                    )
                    return
                }

                val ticketList = mutableListOf<TicketPurchasedDto>()

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val validFromDate = sdf.parse(ticketOrderRequest.validFrom)
                val iat = System.currentTimeMillis()
                val exp = validFromDate.time + ticketOrderRequest.duration

                for (i in 1..ticketOrderRequest.quantity) {
                    val ticketPurchasedDummy = TicketPurchased(
                        UUID.randomUUID(),
                        iat,
                        exp,
                        ticketOrderRequest.zones,
                        "",
                        ticketOrderRequest.ticketType,
                        ticketOrderRequest.validFrom,
                        ticketOrderRequest.orderId,
                        profile
                    )
                    val ticket = ticketPurchasedRepository.save(ticketPurchasedDummy)
                    val ticketJwt = Jwts
                        .builder()
                        .setClaims(mapOf("vz" to ticketOrderRequest.zones, "validFrom" to validFromDate.time, "type" to ticketOrderRequest.ticketType))
                        .setSubject(ticket.id.toString())
                        .setIssuedAt(Date(iat))
                        .setExpiration(Date(exp))    //1 hour validity
                        .signWith(hmacKey)
                        .compact()
                    ticket.jws = ticketJwt
                    ticketPurchasedRepository.save(ticket)
                    ticketList.add(ticket.toDTO())
                }

                sendMessageKafkaToTicketCatalogueService(
                    KafkaTicketRequestTravelerServiceResponse(
                        ticketOrderRequest.orderId,
                        "buy_tickets",
                        true,
                        Date()
                    )
                )

            }
            else if(ticketOrderRequest.cmd == "delete_tickets") {
                ticketPurchasedRepository.deleteAllByOrderId(ticketOrderRequest.orderId)
            }
        }
        catch (e: Exception){
            println(e.message)

            if(ticketOrderRequest.cmd == "buy_tickets"){
                sendMessageKafkaToTicketCatalogueService(
                    KafkaTicketRequestTravelerServiceResponse(
                        ticketOrderRequest.orderId,
                        "buy_tickets",
                        false,
                        Date()
                    )
                )
                ticketPurchasedRepository.deleteAllByOrderId(ticketOrderRequest.orderId)
            }

        }
    }

    fun sendMessageKafkaToTicketCatalogueService(payload: KafkaTicketRequestTravelerServiceResponse){
        val message: Message<KafkaTicketRequestTravelerServiceResponse> = MessageBuilder
            .withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, travelerServiceToTicketCatalogueService.name())
            .build()
        kafkaTemplate.send(message)
        println("Kafka message to Ticket Catalogue Service sent with success")
    }

}