package it.polito.wa2.g17.turnstileservice.exceptions

class TicketValidationFailedException(val ticketJwt: String, val nwCallError: String) : RuntimeException("FAILED validation of ticket $ticketJwt\n Reason: $nwCallError")