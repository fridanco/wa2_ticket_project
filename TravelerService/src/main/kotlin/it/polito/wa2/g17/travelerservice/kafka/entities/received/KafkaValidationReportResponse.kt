package it.polito.wa2.g17.travelerservice.kafka.entities.received

import java.util.*

data class KafkaValidationReportResponse(
    val reportID: UUID,
    val succeeded: Boolean,
    val ticketList: List<TicketValidationInfo>
)