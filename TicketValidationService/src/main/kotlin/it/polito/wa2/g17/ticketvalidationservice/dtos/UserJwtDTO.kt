package it.polito.wa2.g17.ticketvalidationservice.dtos

import it.polito.wa2.g17.ticketvalidationservice.Role

data class UserJwtDTO(
    val nickname: String,
    val role: Role
)