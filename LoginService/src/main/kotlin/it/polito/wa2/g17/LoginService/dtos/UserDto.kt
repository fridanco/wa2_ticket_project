package it.polito.wa2.g17.LoginService.dtos

import it.polito.wa2.g17.LoginService.entities.User

data class UserDto(
    val id: Long,
    val nickname: String,
    val email: String,
    val password: String
)

fun User.toDTO() : UserDto{
    return UserDto(id, nickname, email, password)
}
