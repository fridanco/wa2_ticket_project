package it.polito.wa2.g17.ticketcatalogueservice.exceptions

class TicketOrderNotFoundException : RuntimeException("Ticket order could not found with ID received in kafka by PaymentService")