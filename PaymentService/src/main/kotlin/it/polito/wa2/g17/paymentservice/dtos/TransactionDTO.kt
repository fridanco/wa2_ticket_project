package it.polito.wa2.g17.paymentservice.dtos

import it.polito.wa2.g17.paymentservice.entities.Transaction
import java.util.*

data class TransactionDTO (
    var orderId : UUID,
    var userNickname : String,
    var orderPrice : Double,
    var creditCardNumber: String,
    var cardHolder : String,
    var paymentTimestamp: String,
    var paymentSuccessful: Boolean,
    var paymentRefunded: Boolean
)

fun Transaction.toDTO() : TransactionDTO = TransactionDTO(orderId, userNickname, orderPrice, creditCardNumber, cardHolder, paymentTimestamp, paymentSuccessful, paymentRefunded)