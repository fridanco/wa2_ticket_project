package it.polito.wa2.g17.paymentservice.repositories

import it.polito.wa2.g17.paymentservice.entities.Transaction
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TransactionRepository : CoroutineCrudRepository<Transaction, UUID> {

    @Query("INSERT INTO transactions VALUES (:orderId,:username,:orderPrice,:creditCardNumber,:cardHolder,:paymentTimestamp,:paymentSuccessful,:paymentRefunded)")
    suspend fun insertTransaction(
        orderID: UUID,
        username: String,
        orderPrice: Double,
        creditCardNumber: String,
        cardHolder: String,
        paymentTimestamp: String,
        paymentSuccessful: Boolean,
        paymentRefunded: Boolean
    )

    suspend fun findAllByUserNickname(userNickname: String) : List<Transaction>

    suspend fun findTransactionByOrderId(orderID: UUID) : Transaction?

    suspend fun deleteTransactionByOrderId(orderID: UUID)

}