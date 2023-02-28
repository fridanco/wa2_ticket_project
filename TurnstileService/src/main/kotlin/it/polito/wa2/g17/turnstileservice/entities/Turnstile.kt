package it.polito.wa2.g17.turnstileservice.entities

import javax.persistence.*

@Entity
@Table(name = "turnstile")
 class Turnstile(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    val id: Long,

    var jwt : String,
    @Column(nullable = false)
    var zid : String,

    @Column(nullable = false)
    var disabled : Boolean
)