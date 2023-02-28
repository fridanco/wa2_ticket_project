package it.polito.wa2.g17.travelerservice.dtos

import it.polito.wa2.g17.travelerservice.entities.report.ReportEntry
import java.io.Serializable
import java.util.*

data class ReportEntryDto(
    val id: Long,
    val ticketID: UUID,
    val ticketGeneratedTimestamp: Date,
    val ticketValidatedTimestamp: Date? = null,
    val ticketValidatedTurnstileID: Long? = null,
    val ticketValidatedZone: String? = null,
) : Serializable

fun ReportEntry.toDTO() : ReportEntryDto {
    return ReportEntryDto(
        id, ticketID, ticketGeneratedTimestamp, ticketValidatedTimestamp, ticketValidatedTurnstileID, ticketValidatedZone
    )
}
