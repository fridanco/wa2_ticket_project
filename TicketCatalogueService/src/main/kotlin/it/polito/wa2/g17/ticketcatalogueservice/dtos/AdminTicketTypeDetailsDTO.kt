package it.polito.wa2.g17.ticketcatalogueservice.dtos

import org.springframework.format.annotation.DateTimeFormat
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class AdminTicketTypeDetailsDTO(
    //TODO: better validity checks need to be performed
    @NotNull
    @field:Size(min = 1)
    var type : String,
    @NotNull
    var price : Double,
    @NotNull
    var minAge: Int,
    @NotNull
    var maxAge: Int,
    @NotNull
    @field:Size(min = 1)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    var startDay: String,
    @NotNull
    @field:Size(min = 1)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    var endDay: String,
    @NotNull
    @field:Size(min = 1)
    var zid: String
)
