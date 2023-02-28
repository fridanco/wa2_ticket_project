package it.polito.wa2.g17.ticketcatalogueservice.kafka.entities.received

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class KafkaPaymentServiceResponse (
    @JsonProperty("orderId")
    val orderId: UUID,
    @JsonProperty("cmd")
    val cmd: String,
    @JsonProperty("paymentSuccessful")
    val paymentSuccessful: Boolean,
    @JsonProperty("paymentTimestamp")
    val paymentTimestamp: Date,
    )