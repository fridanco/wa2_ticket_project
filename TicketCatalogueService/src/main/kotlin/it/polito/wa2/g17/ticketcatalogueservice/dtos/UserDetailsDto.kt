package it.polito.wa2.g17.ticketcatalogueservice.dtos

import java.util.*

data class UserDetailsDto(
    var name: String,
    var address: String,
    var dateOfBirth: Date,
    var telephoneNumber: String
    )


