package it.polito.wa2.g17.travelerservice.dtos

import it.polito.wa2.g17.travelerservice.entities.TicketPurchased
import java.util.*

data class TicketPurchasedDto(
    var sub: UUID? = null,
    var iat: Long? = null,
    var exp: Long? = null,
    var zid: String? = null,
    var jws: String? = null
)

fun TicketPurchased.toDTO() : TicketPurchasedDto{
    return TicketPurchasedDto(id, iat, exp, zid, jws)
}
