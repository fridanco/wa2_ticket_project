package it.polito.wa2.g17.ticketcatalogueservice.controllers

import it.polito.wa2.g17.ticketcatalogueservice.dtos.*
import it.polito.wa2.g17.ticketcatalogueservice.exceptions.AdminOperationNotPermittedException
import it.polito.wa2.g17.ticketcatalogueservice.exceptions.BodyRequestException
import it.polito.wa2.g17.ticketcatalogueservice.exceptions.InvalidPrincipalException
import it.polito.wa2.g17.ticketcatalogueservice.services.TicketCatalogueService
import it.polito.wa2.g17.ticketcatalogueservice.utils.AdminPermissions
import it.polito.wa2.g17.ticketcatalogueservice.utils.AdminPermissionsUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
class TicketCatalogueController {

    @Autowired
    lateinit var ticketCatalogueService : TicketCatalogueService

    @Autowired
    lateinit var adminPermissionsUtils: AdminPermissionsUtils

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ticket/catalogue/user/tickets")
    @ResponseBody
    suspend fun getAllTickets() : List<TicketTypeDetailsDTO> {
        return ticketCatalogueService.getAllTicketTypes()
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/ticket/catalogue/user/shop/{ticket_id}")
    @ResponseBody
    suspend fun manageSalesProcess(@RequestHeader(value="Authorization") authorizationHeader: String, @PathVariable ticket_id: Long, @RequestBody @Valid ticketOrderDTO : TicketOrderDTO, br: BindingResult) : TicketOrderResponseDTO {

        if(br.hasErrors()){
            println(br.fieldErrors.toString())
            throw BodyRequestException()
        }

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        ticketOrderDTO.ticket_id = ticket_id

        return ticketCatalogueService.orderTickets(ticketOrderDTO, authenticationUsername, authorizationHeader)
   }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ticket/catalogue/user/orders")
    @ResponseBody
    suspend fun getOrders() : List<ResponseTicketOrderDTO> {

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        return ticketCatalogueService.getUserOrders(authenticationUsername)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ticket/catalogue/user/orders/{orderID}")
    @ResponseBody
    suspend fun getOrderByOrderId(@PathVariable orderID: UUID) : ResponseTicketOrderDTO {

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        return ticketCatalogueService.getUserOrderByOrderID(orderID, authenticationUsername)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ticket/catalogue/admin/tickets")
    @ResponseBody
    suspend fun getTickets(): List<TicketTypeDetailsDTO> {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TICKET_CATALOGUE_SERVICE_MANAGE_TICKETS)){
            throw AdminOperationNotPermittedException()
        }

        return ticketCatalogueService.getAllTicketTypes()
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/ticket/catalogue/admin/tickets")
    @ResponseBody
    suspend fun addTicketToCatalogue(@RequestBody @Valid adminTicketTypeDetailsDTO: AdminTicketTypeDetailsDTO, br: BindingResult) {

        if(br.hasErrors()){
            throw BodyRequestException()
        }

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TICKET_CATALOGUE_SERVICE_MANAGE_TICKETS)){
            throw AdminOperationNotPermittedException()
        }

        ticketCatalogueService.addTicketToCatalogue(adminTicketTypeDetailsDTO)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ticket/catalogue/admin/orders")
    @ResponseBody
    suspend fun getUserOrders(): List<ResponseTicketOrderDTO> {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TICKET_CATALOGUE_SERVICE_MANAGE_ORDERS)){
            throw AdminOperationNotPermittedException()
        }

        return ticketCatalogueService.getAllUserOrders()
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ticket/catalogue/admin/orders/{userID}")
    @ResponseBody
    suspend fun getOrderByUserID(@PathVariable userID: String): List<ResponseTicketOrderDTO> {
        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TICKET_CATALOGUE_SERVICE_MANAGE_ORDERS)){
            throw AdminOperationNotPermittedException()
        }

        return ticketCatalogueService.getUserOrdersAsAdmin(userID)
    }




}