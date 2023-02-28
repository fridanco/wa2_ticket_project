package it.polito.wa2.g17.paymentservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import javax.validation.constraints.NotNull

@Table(name = "transactions")
class Transaction(
    @Id
    @Column
    @NotNull
    var orderId : UUID,
    @Column
    @NotNull
    var userNickname : String,
    @Column
    @NotNull
    var orderPrice : Double,
    @Column
    @NotNull
    var creditCardNumber: String,
    @Column
    @NotNull
    var cardHolder : String,
    @Column
    @NotNull
    var paymentTimestamp: String,
    @Column
    @NotNull
    var paymentSuccessful: Boolean,
    @Column
    @NotNull
    var paymentRefunded: Boolean
)