package it.polito.wa2.g17.turnstileservice.dtos

import org.jetbrains.annotations.NotNull


data class TicketTurnstileDTO (
    @NotNull
    val jws_ticket : String,
)