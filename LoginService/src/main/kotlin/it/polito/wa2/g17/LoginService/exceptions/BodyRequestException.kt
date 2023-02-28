package it.polito.wa2.g17.LoginService.exceptions

class BodyRequestException : RuntimeException("Body doesn't respect the security policy")