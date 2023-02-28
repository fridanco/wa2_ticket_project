package it.polito.wa2.g17.LoginService.dtos

import it.polito.wa2.g17.LoginService.entities.Activation
import java.util.*

data class ActivationDto(
    val provisional_id: UUID,
    val activation_code: UUID,
    val user: UserDto,
    val counter: Int,
    val deadline: Date
)

fun Activation.toDTO() : ActivationDto {
    return ActivationDto(provisionalId, activationCode, user.toDTO(), counter, deadline)
}
