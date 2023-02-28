package it.polito.wa2.g17.ticketvalidationservice.dtos

import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TicketDTO (
    @NotNull
    @NotEmpty
    var turnstileID: String,
    @NotNull
    @NotEmpty
    var ticket_jws: String,
    @NotNull
    @NotEmpty
    var validated_zone: String,
)