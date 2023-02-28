package it.polito.wa2.g17.travelerservice.repositories

import it.polito.wa2.g17.travelerservice.entities.TicketPurchased
import it.polito.wa2.g17.travelerservice.entities.UserDetails
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TicketPurchasedRepository : CrudRepository<TicketPurchased, UUID> {

    fun findAllByUserDetails(userDetails: UserDetails) : List<TicketPurchased>

    fun findAllByUserDetailsAndOrderId(userDetails: UserDetails, orderID: UUID) : List<TicketPurchased>

    fun findByUserDetailsAndId(userDetails: UserDetails, ticketID: UUID) : TicketPurchased?

    fun deleteAllByOrderId(orderID: UUID)

    fun findAllByUserDetailsAndIatBetween(userDetails: UserDetails, from: Long, to: Long) : List<TicketPurchased>

    fun findAllByIatBetween(from: Long, to: Long) : List<TicketPurchased>

}
