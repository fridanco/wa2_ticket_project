package it.polito.wa2.g17.ticketvalidationservice.exceptions

class InvalidPrincipalException : RuntimeException("Could not retrieve Principal object (JwtDTO)")