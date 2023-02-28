package it.polito.wa2.g17.travelerservice.kafka.entities.sent

import java.util.*

class KafkaValidationReportRequest(
    val reportID: UUID,
    val ticketList: List<TicketPurchasedInfo>
)