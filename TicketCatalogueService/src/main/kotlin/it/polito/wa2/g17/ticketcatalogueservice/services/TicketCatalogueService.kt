package it.polito.wa2.g17.ticketcatalogueservice.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.SignatureAlgorithm
import it.polito.wa2.g17.ticketcatalogueservice.dtos.*
import it.polito.wa2.g17.ticketcatalogueservice.entities.TicketOrder
import it.polito.wa2.g17.ticketcatalogueservice.entities.TicketTypeDetails
import it.polito.wa2.g17.ticketcatalogueservice.exceptions.*
import it.polito.wa2.g17.ticketcatalogueservice.kafka.entities.received.KafkaPaymentServiceResponse
import it.polito.wa2.g17.ticketcatalogueservice.kafka.entities.received.KafkaTravelerServiceResponse
import it.polito.wa2.g17.ticketcatalogueservice.kafka.entities.sent.KafkaPaymentServiceRequest
import it.polito.wa2.g17.ticketcatalogueservice.kafka.entities.sent.KafkaTravelerServiceRequest
import it.polito.wa2.g17.ticketcatalogueservice.repositories.TicketOrderRepository
import it.polito.wa2.g17.ticketcatalogueservice.repositories.TicketTypeDetailsRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.admin.NewTopic
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import java.security.Key
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.spec.SecretKeySpec


@Service
class TicketCatalogueService : InitializingBean {
    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var ticketOrderRepository: TicketOrderRepository

    @Autowired
    lateinit var ticketTypeDetailsRepository: TicketTypeDetailsRepository

    @Autowired
    lateinit var loadBalancedWebClientBuilder: WebClient.Builder

    @Qualifier("topic_ticketCatalogueServiceToPaymentService")
    @Autowired
    lateinit var ticketCatalogueServiceToPaymentService: NewTopic

    @Qualifier("topic_ticketCatalogueServiceToTravelerService")
    @Autowired
    lateinit var ticketCatalogueServiceToTravelerService: NewTopic

    @Value("\${ticketValidationJwtSecret}")
    lateinit var secret : String

    lateinit var hmacKey: Key

    override fun afterPropertiesSet() {
        hmacKey = SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.jcaName)
    }

    private val log = LoggerFactory.getLogger(javaClass)

    suspend fun getAllTicketTypes(): List<TicketTypeDetailsDTO> {
        return ticketTypeDetailsRepository.findAll().map { it.toDTO() }.toList()
    }

    suspend fun orderTickets(ticketOrderDTO: TicketOrderDTO, authenticationUsername: String, authorizationHeader: String) : TicketOrderResponseDTO {

        val ticketTypeDetails = ticketTypeDetailsRepository.getTicketTypeDetailsByTicketID(ticketOrderDTO.ticket_id)
            ?: throw TicketTypeNotFoundException()

        println(ticketTypeDetails)

        val client = loadBalancedWebClientBuilder
            .baseUrl("http://Traveler-Service")
            .build()

        val uriSpec: WebClient.UriSpec<WebClient.RequestBodySpec> = client.method(HttpMethod.GET)

        val bodySpec: WebClient.RequestBodySpec = uriSpec.uri("/traveler/user/my/profile")

        val headersSpec: WebClient.RequestHeadersSpec<*> = bodySpec

        val responseSpec: WebClient.ResponseSpec = headersSpec
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
            .accept(MediaType.APPLICATION_JSON)
            .ifNoneMatch("*")
            .ifModifiedSince(ZonedDateTime.now())
            .retrieve()

        val userProfile : UserDetailsDto

        try {
            userProfile = responseSpec.awaitBodyOrNull() ?: throw UserEmptyProfileException()
        }
        catch (e: Exception){
            throw UserDetailsCouldNotBeRetrievedException()
        }

        if(ticketTypeDetails.minAge!=-1 || ticketTypeDetails.maxAge!=-1){

            println(ticketTypeDetails.minAge)
            println(ticketTypeDetails.maxAge)

            val start = userProfile.dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val stop: LocalDate = LocalDate.now()
            val userAgeYears = ChronoUnit.YEARS.between(start, stop)

            if((ticketTypeDetails.minAge!=-1 && userAgeYears<ticketTypeDetails.minAge)
                || (ticketTypeDetails.maxAge!=-1 && userAgeYears>ticketTypeDetails.maxAge)){
                throw TicketPurchaseNotPermittedException()
            }

        }

        val ticketOrder = TicketOrder(
            authenticationUsername,
            ticketOrderDTO.n_tickets,
            ticketTypeDetails.type,
            ticketTypeDetails.ticketID,
            ticketOrderDTO.n_tickets*ticketTypeDetails.price,
            ticketOrderDTO.creditCardNumber.toString(),
            ticketOrderDTO.cardholder,
            Date().toString(),
            "PENDING",
            null,
            "PENDING",
            Date().toString()
        )

        val savedTicketOrder = ticketOrderRepository.save(ticketOrder)

        try {
            println("Send to PaymentService msg from HTTP POST endpoint request")

            sendMessageKafkaToPaymentService(
                KafkaPaymentServiceRequest(
                    savedTicketOrder.orderId,
                    "pay",
                    authenticationUsername,
                    ticketOrderDTO.n_tickets,
                    ticketTypeDetails.type,
                    ticketOrderDTO.n_tickets*ticketTypeDetails.price,
                    ticketOrderDTO.creditCardNumber,
                    ticketOrderDTO.cardholder
                )
            )
        }
        catch (e: Exception) {
            println("KAFKA message failed to send")
            println(e.message)

            ticketOrderRepository.delete(ticketOrder)

            throw KafkaMessageException()
        }

        return TicketOrderResponseDTO(savedTicketOrder.orderId)
    }

    suspend fun getUserOrders(authenticationUsername: String) : List<ResponseTicketOrderDTO> {
        return ticketOrderRepository.getTicketOrdersByUserNickname(authenticationUsername).map { it.toDTO() }
    }

    suspend fun getUserOrderByOrderID(orderId: UUID, authenticationUsername: String) : ResponseTicketOrderDTO{
        return ticketOrderRepository.getTicketOrderByOrderIdAndUserNickname(orderId, authenticationUsername)?.toDTO()
            ?: throw OrderNotFoundException()
    }

    suspend fun addTicketToCatalogue(adminTicketTypeDetailsDTO: AdminTicketTypeDetailsDTO) {
        val ticketType = TicketTypeDetails(
            ticketID = 0,
            adminTicketTypeDetailsDTO.type,
            adminTicketTypeDetailsDTO.price,
            adminTicketTypeDetailsDTO.minAge,
            adminTicketTypeDetailsDTO.maxAge,
            adminTicketTypeDetailsDTO.startDay,
            adminTicketTypeDetailsDTO.endDay,
            adminTicketTypeDetailsDTO.zid
        )

        try {
            ticketTypeDetailsRepository.save(ticketType)
        }
        catch (e: Exception){
            throw TicketTypeCouldNotBeInsertedException()
        }
    }

    suspend fun getAllUserOrders() : List<ResponseTicketOrderDTO> {
        return ticketOrderRepository.findAll().map { it.toDTO() }.toList()
    }

    suspend fun getUserOrdersAsAdmin(userNickname: String) : List<ResponseTicketOrderDTO>{
        return ticketOrderRepository.getTicketOrdersByUserNickname(userNickname).map { it.toDTO() }.toList()
    }

    @KafkaListener(topics = ["paymentService_ticketCatalogueService"], groupId = "it.polito.wa2.ticketcatalogueservice")
    fun listenMessageKafkaFromPaymentService(message: String) {
        println(message)

        val paymentResult: KafkaPaymentServiceResponse

        try {
            val mapper = jacksonObjectMapper()
            paymentResult = mapper.readValue(message, KafkaPaymentServiceResponse::class.java)
        }
        catch (e: Exception){
            println("Exception in PaymentService deserializer")
            println(e.message)
            return
        }

        try {

            val ticketOrder: TicketOrder? = runBlocking {
                ticketOrderRepository.findById(paymentResult.orderId)
            }

            if(ticketOrder==null){

                println("TickerOrder from DB is NULL in PaymentService listener")

                sendMessageKafkaToPaymentService(
                    KafkaPaymentServiceRequest(
                        paymentResult.orderId,
                        "refund",
                        "",
                        0,
                        "",
                        0.0,
                        "",
                        ""
                    )
                )

                return
            }

            if(paymentResult.cmd == "pay") {
                if (paymentResult.paymentSuccessful) {

                    println("PaymentService response successful")

                    val ticketType = runBlocking {
                        ticketTypeDetailsRepository.findById(ticketOrder.ticketTypeId)
                    }

                    if(ticketType==null){
                        sendMessageKafkaToPaymentService(
                            KafkaPaymentServiceRequest(
                                paymentResult.orderId,
                                "refund",
                                "",
                                0,
                                "",
                                0.0,
                                "",
                                ""
                            )
                        )

                        ticketOrder.apply {
                            this.paymentStatus = "REFUNDED"
                            this.paymentTimestamp = Date().toString()
                        }

                        runBlocking {
                            ticketOrderRepository.save(ticketOrder)
                        }

                        return
                    }

                    ticketOrder.apply {
                        this.paymentStatus = "SUCCESSFUL"
                        this.paymentTimestamp = Date().toString()
                    }

                    runBlocking {
                        ticketOrderRepository.save(ticketOrder)
                    }

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
                    val startDate = sdf.parse(ticketType.startDay)
                    val endDate = sdf.parse(ticketType.endDay)

                    val duration = endDate.time - startDate.time

                    sendMessageKafkaToTravelerService(
                        KafkaTravelerServiceRequest(
                            ticketOrder.userNickname,
                            "buy_tickets",
                            ticketOrder.orderId,
                            ticketOrder.numTickets,
                            ticketOrder.ticketType,
                            ticketOrder.orderPrice / ticketOrder.numTickets,
                            ticketType.zid,
                            ticketType.startDay,
                            duration
                        )
                    )

                }
                else {

                    println("PaymentService response failed")

                    ticketOrder.apply {
                        this.paymentStatus = "FAILED"
                        this.paymentTimestamp = paymentResult.paymentTimestamp.toString()
                        this.ticketsGeneratedStatus = "ABORTED"
                        this.paymentTimestamp = Date().toString()
                    }

                    runBlocking {
                        ticketOrderRepository.save(ticketOrder)
                    }
                }
            }

        }
        catch (e: Exception){

            println("Exception in PaymentService listener")
            println(e.message)

        }
    }

    @KafkaListener(topics = ["travelerService_ticketCatalogueService"], groupId = "it.polito.wa2.ticketcatalogueservice")
    fun listenMessageKafkaFromTravelerService(message: String) {

        val ticketOrderResult: KafkaTravelerServiceResponse

        try {
            val mapper = jacksonObjectMapper()
            ticketOrderResult = mapper.readValue(message, KafkaTravelerServiceResponse::class.java)
        }
        catch (e: Exception){
            println("Exception in TravelerService deserializer")
            println(e.message)
            return
        }

        try{

            val order : TicketOrder? = runBlocking {
                ticketOrderRepository.findById(ticketOrderResult.orderId)
            }

            if(order==null){

                println("TicketOder from DB is NULL in TravelerService listener")

                sendMessageKafkaToTravelerService(
                    KafkaTravelerServiceRequest(
                        "",
                        "delete_tickets",
                        ticketOrderResult.orderId,
                        0,
                        "",
                        0.0,
                        "",
                        "",
                        0
                    )
                )

                sendMessageKafkaToPaymentService(
                    KafkaPaymentServiceRequest(
                        ticketOrderResult.orderId,
                        "refund",
                        "",
                        0,
                        "",
                        0.0,
                        "",
                        ""
                    )
                )

                return
            }

            if (ticketOrderResult.cmd == "buy_tickets" ) {

                if (ticketOrderResult.ticketOrderSuccessful) {

                    println("TravelerService response successful")

                    order.apply {
                        ticketsGeneratedStatus = "SUCCESSFUL"
                        ticketsGeneratedTimestamp = ticketOrderResult.ticketOrderTimestamp.toString()
                    }

                    runBlocking {
                        ticketOrderRepository.save(order)
                    }

                }
                else{

                    println("TravelerService response failed")

                    sendMessageKafkaToPaymentService(
                        KafkaPaymentServiceRequest(
                            ticketOrderResult.orderId,
                            "refund",
                            "",
                            0,
                            "",
                            0.0,
                            "",
                            ""
                        )
                    )

                    order.apply {
                        ticketsGeneratedStatus = "FAILED"
                        ticketsGeneratedTimestamp = ticketOrderResult.ticketOrderTimestamp.toString()
                        paymentStatus = "REFUNDED"
                        paymentTimestamp = Date().toString()
                    }

                    runBlocking {
                        ticketOrderRepository.save(order)
                    }

                }


            }
        }
        catch (e: Exception){

            println("Exception in TravelerService listener")
            println(e.message)

        }

    }

    fun sendMessageKafkaToPaymentService (payload: KafkaPaymentServiceRequest) {
        val message: Message<KafkaPaymentServiceRequest> = MessageBuilder
            .withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, ticketCatalogueServiceToPaymentService.name())
            .build()
        kafkaTemplate.send(message)
        log.info("Kafka message to Payment Service sent with success")

    }

    fun sendMessageKafkaToTravelerService(payload: KafkaTravelerServiceRequest){
        val message: Message<KafkaTravelerServiceRequest> = MessageBuilder
            .withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, ticketCatalogueServiceToTravelerService.name())
            .build()
        kafkaTemplate.send(message)
        log.info("Kafka message (cmd = ${payload.cmd}) to Traveler Service sent with success")
    }

}