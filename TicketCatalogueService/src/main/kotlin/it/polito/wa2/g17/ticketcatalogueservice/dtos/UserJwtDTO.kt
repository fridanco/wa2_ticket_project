package it.polito.wa2.g17.ticketcatalogueservice.dtos

import it.polito.wa2.g17.ticketcatalogueservice.Role

data class UserJwtDTO(
    val nickname: String,
    val role: Role,
    val permissions: String
)