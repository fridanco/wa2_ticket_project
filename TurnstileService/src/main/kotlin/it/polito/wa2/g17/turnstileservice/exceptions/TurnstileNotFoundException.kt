package it.polito.wa2.g17.turnstileservice.exceptions

class TurnstileNotFoundException(val id: Long) : RuntimeException("The turnstile with ID $id was not found")