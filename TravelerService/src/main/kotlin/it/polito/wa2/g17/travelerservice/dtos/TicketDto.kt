package it.polito.wa2.g17.travelerservice.dtos

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class TicketDto(
    @NotNull
    @field:Size(min = 1)
    var cmd: String,

    @NotNull
    var quantity: Int,

    @NotNull
    @field:Size(min=1)
    var zones: String
)
