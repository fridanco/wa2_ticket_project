package it.polito.wa2.g17.ticketvalidationservice.exceptions

class ExpiredJwtException : RuntimeException("Ticket validity has expired")