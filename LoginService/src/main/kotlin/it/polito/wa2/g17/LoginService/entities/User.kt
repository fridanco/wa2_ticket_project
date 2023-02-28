package it.polito.wa2.g17.LoginService.entities

import it.polito.wa2.g17.LoginService.Role
import javax.persistence.*

@Entity
@Table(name = "users")
class User{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    var id: Long = 0

    @Column(nullable = false)
    var nickname : String = ""
    @Column(nullable = false)
    var email : String = ""
    @Column(nullable = false)
    var password : String = ""

    @Column(nullable = false, updatable = true)
    var role : Role = Role.ROLE_CUSTOMER

    @Column(nullable = false, updatable = true)
    var loginServiceManageUsers : Boolean = false
    @Column(nullable = false, updatable = true)
    var loginServiceManageAdmins : Boolean = false
    @Column(nullable = false, updatable = true)
    var turnstileServiceManageTurnstile : Boolean = false
    @Column(nullable = false, updatable = true)
    var travelerServiceManageTravelers : Boolean = false
    @Column(nullable = false, updatable = true)
    var travelerServiceManageReports : Boolean = false
    @Column(nullable = false, updatable = true)
    var ticketCatalogueServiceManageTickets : Boolean = false
    @Column(nullable = false, updatable = true)
    var ticketCatalogueServiceManageOrders : Boolean = false
    @Column(nullable = false, updatable = true)
    var paymentServiceManageTransactions : Boolean = false

    @Column(nullable = false, updatable = true)
    var createdAt = System.currentTimeMillis()

    @Column(nullable = false, updatable = true)
    var valid : Boolean = false
    @Column(nullable = false, updatable = true)
    var disabled = false
}