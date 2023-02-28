package it.polito.wa2.g17.travelerservice.kafka.entities.sent

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class KafkaTicketRequestTravelerServiceResponse (
    @JsonProperty("orderId")
    val orderId: UUID,
    @JsonProperty("cmd")
    val cmd: String,
    @JsonProperty("ticketOrderSuccessful")
    val ticketOrderSuccessful: Boolean,
    @JsonProperty("ticketOrderTimestamp")
    val ticketOrderTimestamp: Date
    )