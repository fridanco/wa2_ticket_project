package it.polito.wa2.g17.ticketvalidationservice.kafka.entities.received

import java.util.*

class KafkaValidationReportRequest(
    val reportID: UUID,
    val ticketList: List<TicketPurchasedInfo>
)