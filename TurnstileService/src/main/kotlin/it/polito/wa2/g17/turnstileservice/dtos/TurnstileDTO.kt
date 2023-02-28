package it.polito.wa2.g17.turnstileservice.dtos

import it.polito.wa2.g17.turnstileservice.entities.Turnstile

data class TurnstileDTO(
    val id :Long,
    val jwt : String,
    val zid : String,
    val disabled : Boolean
)

fun Turnstile.toDTO() : TurnstileDTO {
    return TurnstileDTO(id, jwt, zid, disabled)
}