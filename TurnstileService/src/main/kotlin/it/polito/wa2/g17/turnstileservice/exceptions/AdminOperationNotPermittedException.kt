package it.polito.wa2.g17.turnstileservice.exceptions

class AdminOperationNotPermittedException : RuntimeException("You do not have the necessary permissions to perform this operation")