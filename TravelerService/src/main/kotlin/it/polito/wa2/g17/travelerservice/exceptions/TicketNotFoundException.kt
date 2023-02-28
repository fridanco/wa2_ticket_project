package it.polito.wa2.g17.travelerservice.exceptions

import java.util.*

class TicketNotFoundException(val ticketID: UUID) : RuntimeException("Ticket with ID $ticketID could not be found") {

}
