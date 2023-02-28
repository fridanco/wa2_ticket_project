package it.polito.wa2.g17.ticketcatalogueservice.dtos

import it.polito.wa2.g17.ticketcatalogueservice.entities.TicketOrder
import java.util.*

data class ResponseTicketOrderDTO (
    //Expiration and CVV not stored
    var orderId : UUID,
    var userNickname : String,
    var numTickets : Int,
    var ticketType : String,
    var ticketTypeId : Long,
    var orderPrice : Double,
    var creditCardNumber: String,
    var cardHolder : String,
    var ticketsGeneratedStatus : String,
    var ticketsGeneratedTimeStamp: String?,
    var paymentStatus : String,
    var paymentTimestamp : String?,
    )

fun TicketOrder.toDTO() : ResponseTicketOrderDTO = ResponseTicketOrderDTO(orderId, userNickname, numTickets, ticketType, ticketTypeId, orderPrice, creditCardNumber, cardHolder, ticketsGeneratedStatus, ticketsGeneratedTimestamp, paymentStatus, paymentTimestamp)