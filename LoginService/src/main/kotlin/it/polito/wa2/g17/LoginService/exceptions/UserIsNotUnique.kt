package it.polito.wa2.g17.LoginService.exceptions

class UserIsNotUnique: RuntimeException("The user is not unique -- username or email are already used")