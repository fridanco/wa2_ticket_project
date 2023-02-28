package it.polito.wa2.g17.ticketcatalogueservice.dtos

import org.springframework.format.annotation.DateTimeFormat
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class TicketOrderDTO (
    @NotNull
    @field:Min(1)
    var n_tickets : Int,

    @NotNull
    var ticket_id: Long,

    @NotNull
    @field : Size(min = 16, max = 16)
    var creditCardNumber: String,

    @NotNull
    @DateTimeFormat(pattern = "MM/yy")
    var expirationDate: String,

    @NotNull
    @field : Size(min = 3)
    var cvv: String,

    @NotNull
    @field : Size(min = 1)
    var cardholder : String,
    )