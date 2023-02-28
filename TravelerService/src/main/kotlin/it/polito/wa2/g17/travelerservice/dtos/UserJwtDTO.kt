package it.polito.wa2.g17.travelerservice.dtos

import it.polito.wa2.g17.travelerservice.Role

data class UserJwtDTO(
    val nickname: String,
    val role: Role,
    val permissions: String
)