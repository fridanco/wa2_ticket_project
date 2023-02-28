package it.polito.wa2.g17.ticketvalidationservice.repositories

import it.polito.wa2.g17.ticketvalidationservice.entities.ValidatedTicket
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ValidatedTicketRepository : CrudRepository<ValidatedTicket, UUID>{

    fun findAllByIdIn(ticketIdsList: List<UUID>) : List<ValidatedTicket>

}