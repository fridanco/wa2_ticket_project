package it.polito.wa2.g17.ticketcatalogueservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotNull


@Table(name="ticket_details")
class TicketTypeDetails (
    @Id
    @Column
    @NotNull
    var ticketID : Long,
    @Column
    @NotNull
    var type : String,
    @Column
    @NotNull
    var price : Double,
    @Column
    @NotNull
    var minAge: Int,
    @Column
    @NotNull
    var maxAge: Int,
    @Column
    @NotNull
    var startDay: String,
    @Column
    @NotNull
    var endDay: String,
    @Column
    @NotNull
    var zid: String
        )