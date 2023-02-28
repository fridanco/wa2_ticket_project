package it.polito.wa2.g17.paymentservice.kafka.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class KafkaPaymentRequest(
    @JsonProperty("orderId")
    val orderId: UUID,
    @JsonProperty("cmd")
    val cmd: String,
    @JsonProperty("username")
    val username: String,
    @JsonProperty("n_tickets")
    val n_tickets : Int,
    @JsonProperty("ticketType")
    val ticketType : String,
    @JsonProperty("price")
    val price: Double,
    @JsonProperty("creditCardNumber")
    val creditCardNumber : String,
    @JsonProperty("cardHolder")
    val cardHolder : String
)