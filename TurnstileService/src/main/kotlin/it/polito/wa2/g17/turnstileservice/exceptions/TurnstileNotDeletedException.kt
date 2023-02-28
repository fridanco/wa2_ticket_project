package it.polito.wa2.g17.turnstileservice.exceptions

class TurnstileNotDeletedException: RuntimeException("Could not delete turnstile, please check that the provided ID is valid")