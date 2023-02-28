package it.polito.wa2.g17.LoginService.entities

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "activation")
class Activation(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    var provisionalId: UUID,
    @Column(nullable = false)
    var activationCode: UUID,
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "user_id")
    var user: User,
    @Column(nullable = false, updatable = true)
    var counter: Int,
    @Column(nullable = false)
    val deadline: Date
)