package it.polito.wa2.g17.paymentservice.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import it.polito.wa2.g17.paymentservice.dtos.TransactionDTO
import it.polito.wa2.g17.paymentservice.dtos.toDTO
import it.polito.wa2.g17.paymentservice.entities.Transaction
import it.polito.wa2.g17.paymentservice.kafka.entities.KafkaPaymentRequest
import it.polito.wa2.g17.paymentservice.kafka.entities.KafkaPaymentResponse
import it.polito.wa2.g17.paymentservice.repositories.TransactionRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class PaymentService{

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    lateinit var transactionRepository : TransactionRepository

    @Qualifier("topic_paymentServiceToTicketCatalogueService")
    @Autowired
    lateinit var paymentServiceToTicketCatalogueService: NewTopic

    @Qualifier("topic_ticketCatalogueServiceToPaymentService")
    @Autowired
    lateinit var ticketCatalogueServiceToPaymentService: NewTopic

    suspend fun getUserTransactions(authenticatedUsername: String) : List<TransactionDTO>{
        return transactionRepository.findAllByUserNickname(authenticatedUsername).map { it.toDTO() }
    }

    suspend fun getAllTransactions() : List<TransactionDTO> {
        return transactionRepository.findAll().map { it.toDTO() }.toList()
    }

    @KafkaListener(topics = ["ticketCatalogueService_paymentService"], groupId = "it.polito.wa2.paymentservice")
    fun listerForTicketOrder(message: String) {

        println("Received message from TicketCatalogueService")

        val ticketOrder: KafkaPaymentRequest

        try{
            val mapper = jacksonObjectMapper()
            ticketOrder= mapper.readValue(message, KafkaPaymentRequest::class.java)
        }
        catch (e: Exception){
            println("Exception in TicketCatalogueService deserialized")
            println(e.message)
            return
        }

        try {

            if(ticketOrder.cmd == "pay") {

                val transaction = Transaction(
                    ticketOrder.orderId,
                    ticketOrder.username,
                    ticketOrder.price,
                    ticketOrder.creditCardNumber,
                    ticketOrder.cardHolder,
                    Date().toString(),
                    Random.nextBoolean(),
                    false
                )

                runBlocking {
                    //transactionRepository.save(transaction)

                    transactionRepository.insertTransaction(
                        ticketOrder.orderId,
                        ticketOrder.username,
                        ticketOrder.price,
                        ticketOrder.creditCardNumber,
                        ticketOrder.cardHolder,
                        transaction.paymentTimestamp,
                        transaction.paymentSuccessful,
                        transaction.paymentRefunded
                    )
                }

                println("Payment operation was ${transaction.paymentSuccessful}")
                sendMessageKafkaToTicketCatalogueService(
                    KafkaPaymentResponse(
                        ticketOrder.orderId,
                        "pay",
                        transaction.paymentSuccessful,
                        Date()
                    )
                )
            }
            else if(ticketOrder.cmd == "refund"){

                runBlocking {
                    val transaction = transactionRepository.findTransactionByOrderId(ticketOrder.orderId) ?: return@runBlocking
                    transaction.paymentRefunded = true
                    transactionRepository.save(transaction)
                }

            }

        }
        catch (e: Exception){

            println("Exception in TicketCatalogoueService listener")
            println(e.message)

            if(ticketOrder.cmd == "pay") {
                sendMessageKafkaToTicketCatalogueService(
                    KafkaPaymentResponse(
                        ticketOrder.orderId,
                        "pay",
                        false,
                        Date()
                    )
                )
            }
        }


    }

    fun sendMessageKafkaToTicketCatalogueService (payload: KafkaPaymentResponse) {
        val message: Message<KafkaPaymentResponse> = MessageBuilder
            .withPayload(payload)
            .setHeader(KafkaHeaders.TOPIC, paymentServiceToTicketCatalogueService.name())
            .setHeader("X-Custom-Header", "Custom header here")
            .build()
        kafkaTemplate.send(message)
    }

}