package it.polito.wa2.g17.ticketvalidationservice.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.ticketvalidationservice.dtos.TicketDTO
import it.polito.wa2.g17.ticketvalidationservice.entities.ValidatedTicket
import it.polito.wa2.g17.ticketvalidationservice.exceptions.DuplicateTicketException
import it.polito.wa2.g17.ticketvalidationservice.exceptions.ExpiredJwtException
import it.polito.wa2.g17.ticketvalidationservice.exceptions.InvalidZoneException
import it.polito.wa2.g17.ticketvalidationservice.exceptions.TicketNotYetValidException
import it.polito.wa2.g17.ticketvalidationservice.kafka.entities.received.KafkaValidationReportRequest
import it.polito.wa2.g17.ticketvalidationservice.kafka.entities.sent.KafkaValidationReportResponse
import it.polito.wa2.g17.ticketvalidationservice.kafka.entities.sent.TicketValidationInfo
import it.polito.wa2.g17.ticketvalidationservice.repositories.ValidatedTicketRepository
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class TicketValidationService : InitializingBean {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Qualifier("topic_ticketValidationServiceToTravelerService")
    @Autowired
    lateinit var ticketValidationServiceToTravelerService: NewTopic

    //Encode JWT with this secret and select Base64 encoded
    @Value("\${ticketValidationJwtSecret}")
    lateinit var secret : String

    lateinit var hmacKey : Key

    @Autowired
    lateinit var validatedTicketRepository: ValidatedTicketRepository

    override fun afterPropertiesSet() {
        hmacKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)
    }

    @Transactional
    fun validateTicket(ticketDTO: TicketDTO) : Boolean{

        //JWT validation
        val validatedJwt : Jws<Claims> = Jwts
            .parserBuilder()
            .setSigningKey(hmacKey)
            .build()
            .parseClaimsJws(ticketDTO.ticket_jws)

        //Get token expiration from JWT body (payload) claims & perform checks
        val expiration = validatedJwt.body.expiration
        if(expiration==null || expiration.before(Date())){
            throw ExpiredJwtException()
        }

        val validFrom = validatedJwt.body.get("validFrom").toString()
        if(validFrom.toLong() > System.currentTimeMillis()){
            throw TicketNotYetValidException()
        }

        //Get ticket vz from JWT body (payload) claims & perform checks
        var validTicketVZ = false
        validatedJwt.body.get("vz",String::class.java)?.forEach {
            if(it==ticketDTO.validated_zone.toCharArray()[0]){
                validTicketVZ=true
            }
        }
        if(!validTicketVZ){
            throw InvalidZoneException()
        }

        //Check ticket unicity (check in DB)
        val ticketID = validatedJwt.body?.get("sub",String::class.java)
        if(ticketID!=null && ticketID.isNotEmpty()){
            if(!validatedTicketRepository.findById(UUID.fromString(ticketID)).isEmpty){
                throw DuplicateTicketException()
            }
            validatedTicketRepository.save(ValidatedTicket(
                UUID.fromString(ticketID),
                ticketDTO.turnstileID.toLong(),
                ticketDTO.ticket_jws,
                ticketDTO.validated_zone,
                Date()
            ))

        }

        return true
    }

    fun validateTicketTurnstile(ticketDTO: TicketDTO){

        //TODO: Check Auth JWT
        this.validateTicket(ticketDTO)

    }

    @KafkaListener(topics = ["travelerService_ticketValidationService"], groupId = "it.polito.wa2.ticketvalidationservice")
    fun getTicketValidationsRequest(message: String){

        var ticketValidationRequest: KafkaValidationReportRequest? = null

        try {
            val mapper = jacksonObjectMapper()
            ticketValidationRequest = mapper.readValue(message, KafkaValidationReportRequest::class.java)
        }
        catch (e: Exception) {
            println(e.message)
            return
        }

        try{

            val validatedTickets = validatedTicketRepository.findAllByIdIn(
                ticketValidationRequest.ticketList.map { it.ticketID }
            )

            val reportTicketList = mutableListOf<TicketValidationInfo>()

            ticketValidationRequest.ticketList.forEach { purchasedTicket ->

                val validatedTicket = validatedTickets.find { it.id == purchasedTicket.ticketID }

                if(validatedTicket==null){
                    reportTicketList.add(
                        TicketValidationInfo(
                            purchasedTicket.ticketID,
                            purchasedTicket.ticketGeneratedTimestamp,
                            null,
                            null,
                            null
                        )
                    )
                }
                else{
                    reportTicketList.add(
                        TicketValidationInfo(
                            purchasedTicket.ticketID,
                            purchasedTicket.ticketGeneratedTimestamp,
                            validatedTicket.timestamp,
                            validatedTicket.turnstileId,
                            validatedTicket.validatedZone
                        )
                    )
                }

            }

            sendMessageKafkaToTravelerService(
                KafkaValidationReportResponse(
                    ticketValidationRequest.reportID,
                    true,
                    reportTicketList
                )
            )

        }
        catch (e: Exception){
            println(e.message)

            sendMessageKafkaToTravelerService(
                KafkaValidationReportResponse(
                    ticketValidationRequest.reportID,
                    false,
                    listOf()
                )
            )
        }
    }

    fun sendMessageKafkaToTravelerService(payload: KafkaValidationReportResponse){
        val message: Message<KafkaValidationReportResponse> = MessageBuilder
            .withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, ticketValidationServiceToTravelerService.name())
            .build()
        kafkaTemplate.send(message)
        println("Kafka message to Traveler Service sent with success")
    }


}