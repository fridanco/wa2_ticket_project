package it.polito.wa2.g17.ticketcatalogueservice.dtos

import it.polito.wa2.g17.ticketcatalogueservice.entities.TicketTypeDetails

data class TicketTypeDetailsDTO(
    var ticketID : Long,
    var type : String,
    var price : Double,
    var minAge: Int,
    var maxAge: Int,
    var startDay: String,
    var endDay: String,
    var zid: String
)

fun TicketTypeDetails.toDTO() : TicketTypeDetailsDTO = TicketTypeDetailsDTO(ticketID, type, price, minAge, maxAge, startDay, endDay, zid)