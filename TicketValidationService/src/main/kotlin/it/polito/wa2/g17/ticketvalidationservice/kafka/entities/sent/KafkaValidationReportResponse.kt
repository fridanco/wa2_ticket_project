package it.polito.wa2.g17.ticketvalidationservice.kafka.entities.sent

import java.util.*

data class KafkaValidationReportResponse(
    val reportID: UUID,
    val succeeded: Boolean,
    val ticketList: List<TicketValidationInfo>
)