package it.polito.wa2.g17.ticketvalidationservice.kafka.entities.sent

import java.util.*

data class TicketValidationInfo(
    val ticketID: UUID,
    val ticketGeneratedTimestamp: Date,
    val ticketValidatedTimestamp: Date?,
    val ticketValidatedTurnstileID: Long?,
    val ticketValidatedZone: String?
)