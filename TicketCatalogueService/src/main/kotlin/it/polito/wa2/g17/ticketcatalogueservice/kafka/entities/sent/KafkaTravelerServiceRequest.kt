package it.polito.wa2.g17.ticketcatalogueservice.kafka.entities.sent

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class KafkaTravelerServiceRequest(
    @JsonProperty("username")
    var username: String,
    @JsonProperty("cmd")
    var cmd: String,
    @JsonProperty("orderId")
    var orderId: UUID,
    @JsonProperty("quantity")
    var quantity: Int,
    @JsonProperty("ticketType")
    var ticketType: String,
    @JsonProperty("ticketPrice")
    var ticketPrice: Double,
    @JsonProperty("zones")
    var zones: String,
    @JsonProperty("validFrom")
    var validFrom: String,
    @JsonProperty("duration")
    var duration: Long
)
