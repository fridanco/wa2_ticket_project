package it.polito.wa2.g17.turnstileservice.exceptions

class NetworkCallFailedException(val url: String, val reason: String?) : RuntimeException("Network call to $url failed\nReason: $reason")