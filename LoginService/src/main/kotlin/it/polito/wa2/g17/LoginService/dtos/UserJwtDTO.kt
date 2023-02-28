package it.polito.wa2.g17.LoginService.dtos

import it.polito.wa2.g17.LoginService.Role

data class UserJwtDTO(
    val nickname: String,
    val role: Role,
    val permissions: String
)