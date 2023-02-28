package it.polito.wa2.g17.ticketvalidationservice.entities

import org.hibernate.annotations.CreationTimestamp
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "validated_tickets")
class ValidatedTicket (
    @Id
    @Column(nullable = false, updatable = false)
    var id: UUID,
    @Column(nullable = false, updatable = false)
    var turnstileId: Long,
    @Column(nullable = false, updatable = false)
    var ticketJwt: String,
    @Column(nullable = false, updatable = false)
    var validatedZone: String,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var timestamp: Date
    )