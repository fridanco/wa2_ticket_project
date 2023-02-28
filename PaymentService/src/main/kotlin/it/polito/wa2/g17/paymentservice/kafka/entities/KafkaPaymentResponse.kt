package it.polito.wa2.g17.paymentservice.kafka.entities

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class KafkaPaymentResponse (
    @JsonProperty("orderId")
    val orderId: UUID,
    @JsonProperty("cmd")
    val cmd: String,
    @JsonProperty("paymentSuccessful")
    val paymentSuccessful: Boolean,
    @JsonProperty("paymentTimestamp")
    val paymentTimestamp: Date,
    )