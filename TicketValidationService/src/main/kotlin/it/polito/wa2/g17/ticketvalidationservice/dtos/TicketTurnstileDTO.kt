package it.polito.wa2.g17.ticketvalidationservice.dtos
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class TicketTurnstileDTO(
    @NotNull
    @NotEmpty
    var ticketJwt: String,
)
