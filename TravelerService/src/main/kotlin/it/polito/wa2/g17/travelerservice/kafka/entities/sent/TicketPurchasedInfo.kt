package it.polito.wa2.g17.travelerservice.kafka.entities.sent

import java.util.*

data class TicketPurchasedInfo(
    val ticketID: UUID,
    val ticketJws: String,
    val ticketGeneratedTimestamp: Date,
    val ticketType: String
)