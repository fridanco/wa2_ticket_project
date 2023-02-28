package it.polito.wa2.g17.travelerservice.kafka.entities.received

import java.util.*

data class TicketValidationInfo(
    val ticketID: UUID,
    val ticketGeneratedTimestamp: Date,
    val ticketValidatedTimestamp: Date?,
    val ticketValidatedTurnstileID: Long?,
    val ticketValidatedZone: String?
)