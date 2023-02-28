package it.polito.wa2.g17.travelerservice.dtos

import it.polito.wa2.g17.travelerservice.entities.UserDetails
import java.util.*

data class UserDetailsDto(
    var id: String,
    var name: String,
    var address: String,
    var dateOfBirth: Date,
    var telephoneNumber: String
)

fun UserDetails.toDTO() : UserDetailsDto {
    return UserDetailsDto(id, name, address, dateOfBirth, telephoneNumber)
}


