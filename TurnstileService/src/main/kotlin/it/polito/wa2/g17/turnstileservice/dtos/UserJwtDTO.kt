package it.polito.wa2.g17.turnstileservice.dtos

import it.polito.wa2.g17.turnstileservice.Role

data class UserJwtDTO(
    val nickname: String,
    val role: Role,
    val permissions: String
)