package it.polito.wa2.g17.ticketcatalogueservice.repositories

import it.polito.wa2.g17.ticketcatalogueservice.entities.TicketTypeDetails
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository


@Repository
interface TicketTypeDetailsRepository : CoroutineCrudRepository<TicketTypeDetails, Long> {
    suspend fun getTicketTypeDetailsByTicketID(ticketID: Long) : TicketTypeDetails?

}