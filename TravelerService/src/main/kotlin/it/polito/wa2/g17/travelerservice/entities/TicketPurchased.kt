package it.polito.wa2.g17.travelerservice.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ticket_purchased")
 class TicketPurchased(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: UUID,
    @Column(name = "iat", nullable = false)
    var iat : Long,
    @Column(name = "exp", nullable = false)
    var exp : Long,
    @Column(name = "zid", nullable = false)
    var zid : String,
    @Column(name = "jws", nullable = false)
    var jws : String,
    @Column(name = "ticket_type", nullable = false)
    var ticketType : String,
    @Column(name = "valid_from", nullable = false)
    var validFrom : String,
    @Column(name = "order_id", nullable = false)
    var orderId: UUID,
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = ForeignKey(name = "fk_user_id"), nullable = false)
    var userDetails: UserDetails,
)