package it.polito.wa2.g17.travelerservice.dtos

import it.polito.wa2.g17.travelerservice.entities.report.Report
import java.io.Serializable
import java.util.*

data class ReportDTO(
    val id: UUID,
    val status: String,
    val reportGeneratedTimestamp: Date? = null,
    val numTickets: Int? = null,
    val numValidatedTickets : Int? = null,
    val reportEntries: List<ReportEntryDto>,
    val userDetails: UserDetailsDto? = null,
) : Serializable

fun Report.toDTO() : ReportDTO{
    return ReportDTO(
        id,
        status,
        reportGeneratedTimestamp,
        numTickets,
        numValidations,
        reportEntries.map { it.toDTO() },
        userDetails?.toDTO()
    )
}
