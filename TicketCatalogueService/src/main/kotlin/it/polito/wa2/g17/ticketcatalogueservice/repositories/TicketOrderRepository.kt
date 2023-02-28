package it.polito.wa2.g17.ticketcatalogueservice.repositories

import it.polito.wa2.g17.ticketcatalogueservice.entities.TicketOrder
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TicketOrderRepository : CoroutineCrudRepository<TicketOrder, UUID> {

    //TODO: Should be list
    suspend fun getTicketOrdersByOrderId(orderId : UUID) : Flow<TicketOrder>?

    suspend fun getTicketOrdersByUserNickname(userNickname: String) : List<TicketOrder>

    suspend fun getTicketOrderByOrderIdAndUserNickname(orderId: UUID, userNickname: String) : TicketOrder?

    suspend fun deleteTicketOrdersByOrderId(orderId: UUID)
}