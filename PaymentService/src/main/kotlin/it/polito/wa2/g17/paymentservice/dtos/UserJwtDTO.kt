package it.polito.wa2.g17.paymentservice.dtos

import it.polito.wa2.g17.paymentservice.Role

data class UserJwtDTO(
    val nickname: String,
    val role: Role,
    val permissions: String
)