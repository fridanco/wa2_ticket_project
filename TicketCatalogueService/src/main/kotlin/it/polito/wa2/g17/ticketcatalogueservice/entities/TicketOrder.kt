package it.polito.wa2.g17.ticketcatalogueservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Embedded.Nullable
import org.springframework.data.relational.core.mapping.Table
import java.util.*
import javax.validation.constraints.NotNull


@Table(name = "ticket_orders")
class TicketOrder (
    @Column
    @NotNull
    var userNickname : String,
    @Column
    @NotNull
    var numTickets : Int,
    @Column
    var ticketType : String,
    @Column
    var ticketTypeId : Long,
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
    var orderPlacedTimestamp : String,
    @Column
    @NotNull
    var ticketsGeneratedStatus : String,
    @Column
    @Nullable
    var ticketsGeneratedTimestamp: String?,
    @Column
    @NotNull
    var paymentStatus : String,
    @Column
    @Nullable
    var paymentTimestamp : String?,
    //Expiration and CVV not stored
    ){
    @Id
    @Column
    @NotNull
    lateinit var orderId : UUID
}