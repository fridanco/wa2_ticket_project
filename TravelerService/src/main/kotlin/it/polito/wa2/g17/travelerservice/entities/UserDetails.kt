package it.polito.wa2.g17.travelerservice.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user_details")
 class UserDetails(
    @Id
    @Column(name = "id", nullable = false)
    val id: String,
    @Column(name = "name", nullable = false)
    var name : String,
    @Column(name = "address", nullable = false)
    var address : String,
    @Column(name = "date_of_birth", nullable=false)
    var dateOfBirth : Date,
    @Column(name = "telephone_number", nullable = false)
    var telephoneNumber : String,
    @OneToMany(mappedBy = "userDetails")
    var tickets : MutableList<TicketPurchased>
)