package it.polito.wa2.g17.turnstileservice.dtos

import org.jetbrains.annotations.NotNull

data class TurnstileAddDTO(
    @NotNull
    val zid : String,
    @NotNull
    val disabled : Boolean,
)
