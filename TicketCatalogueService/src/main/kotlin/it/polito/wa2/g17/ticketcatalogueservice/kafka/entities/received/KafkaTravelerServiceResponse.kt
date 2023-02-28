package it.polito.wa2.g17.ticketcatalogueservice.kafka.entities.received

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class KafkaTravelerServiceResponse (
    @JsonProperty("orderId")
    val orderId: UUID,
    @JsonProperty("cmd")
    val cmd: String,
    @JsonProperty("ticketOrderSuccessful")
    val ticketOrderSuccessful: Boolean,
    @JsonProperty("ticketOrderTimestamp")
    val ticketOrderTimestamp: Date
    )