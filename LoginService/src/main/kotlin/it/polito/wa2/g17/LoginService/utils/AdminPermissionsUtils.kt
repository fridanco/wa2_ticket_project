package it.polito.wa2.g17.LoginService.utils

import it.polito.wa2.g17.LoginService.entities.User
import org.springframework.stereotype.Component

@Component
class AdminPermissionsUtils {

    fun serializeAdminPermissions(user: User) : String{

        return "${user.loginServiceManageUsers.toInt()}" +
                "${user.loginServiceManageAdmins.toInt()}" +
                "${user.turnstileServiceManageTurnstile.toInt()}" +
                "${user.travelerServiceManageTravelers.toInt()}" +
                "${user.travelerServiceManageReports.toInt()}" +
                "${user.ticketCatalogueServiceManageTickets.toInt()}" +
                "${user.ticketCatalogueServiceManageOrders.toInt()}" +
                "${user.paymentServiceManageTransactions.toInt()}"

    }

    fun serializeCustomerPermissions(): String {
        return "00000000"
    }

    fun adminHasPermission(permissions: String, requirePermission: AdminPermissions) : Boolean{

        when(requirePermission){
            AdminPermissions.LOGIN_SERVICE_MANAGE_USERS -> {
                return permissions[0] == '1'
            }
            AdminPermissions.LOGIN_SERVICE_MANAGE_ADMINS -> {
                return permissions[1] == '1'
            }
            AdminPermissions.TURNSTILE_SERVICE_MANAGE_TURNSTILE -> {
                return permissions[2] == '1'
            }
            AdminPermissions.TRAVELER_SERVICE_MANAGE_TRAVELERS -> {
                return permissions[3] == '1'
            }
            AdminPermissions.TRAVELER_SERVICE_MANAGE_REPORTS -> {
                return permissions[4] == '1'
            }
            AdminPermissions.TICKET_CATALOGUE_SERVICE_MANAGE_TICKETS -> {
                return permissions[5] == '1'
            }
            AdminPermissions.TICKET_CATALOGUE_SERVICE_MANAGE_ORDERS -> {
                return permissions[6] == '1'
            }
            AdminPermissions.PAYMENT_SERVICE_MANAGE_TRANSACTIONS -> {
                return permissions[7] == '1'
            }
            else -> {
                return false
            }
        }



    }

    fun Boolean.toInt() = if(this) 1 else 0

}