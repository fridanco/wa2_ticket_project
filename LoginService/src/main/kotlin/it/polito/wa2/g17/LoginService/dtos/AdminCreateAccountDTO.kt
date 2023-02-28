package it.polito.wa2.g17.LoginService.dtos

import javax.validation.constraints.*


data class AdminCreateAccountDTO (
    @NotNull
    @field:Size(min=1)
    val nickname : String,
    
    @NotNull
    @field:Size(min=1)
    @Email
    val email : String,

    @NotNull
    @field:Size(min=8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    val password : String,

    @NotNull
    @NotEmpty
    var role : String,

    @NotNull
    var loginServiceManageUsers : Boolean = false,
    @NotNull
    var loginServiceManageAdmins : Boolean = false,
    @NotNull
    var turnstileServiceManageTurnstile : Boolean = false,
    @NotNull
    var travelerServiceManageTravelers : Boolean = false,
    @NotNull
    var travelerServiceManageReports : Boolean = false,
    @NotNull
    var ticketCatalogueServiceManageTickets : Boolean = false,
    @NotNull
    var ticketCatalogueServiceManageOrders : Boolean = false,
    @NotNull
    var paymentServiceManageTransactions : Boolean = false,
)