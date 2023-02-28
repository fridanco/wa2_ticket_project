package it.polito.wa2.g17.paymentservice.controllers

import it.polito.wa2.g17.paymentservice.dtos.TransactionDTO
import it.polito.wa2.g17.paymentservice.dtos.UserJwtDTO
import it.polito.wa2.g17.paymentservice.exceptions.AdminOperationNotPermittedException
import it.polito.wa2.g17.paymentservice.exceptions.InvalidPrincipalException
import it.polito.wa2.g17.paymentservice.services.PaymentService
import it.polito.wa2.g17.paymentservice.utils.AdminPermissions
import it.polito.wa2.g17.paymentservice.utils.AdminPermissionsUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class PaymentServiceController {

    @Autowired
    lateinit var paymentService: PaymentService

    @Autowired
    lateinit var adminPermissionsUtils: AdminPermissionsUtils

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payment/user/transactions")
    @ResponseBody
    suspend fun getUserTransactions() : List<TransactionDTO> {

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        return paymentService.getUserTransactions(authenticationUsername)
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/payment/admin/transactions")
    @ResponseBody
    suspend fun getAllTransactions() : List<TransactionDTO> {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.PAYMENT_SERVICE_MANAGE_TRANSACTIONS)){
            throw AdminOperationNotPermittedException()
        }

        return paymentService.getAllTransactions()
   }
}