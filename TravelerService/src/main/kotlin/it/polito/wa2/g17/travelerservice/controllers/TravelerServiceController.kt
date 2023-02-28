package it.polito.wa2.g17.travelerservice.controllers

import it.polito.wa2.g17.travelerservice.dtos.*
import it.polito.wa2.g17.travelerservice.exceptions.AdminOperationNotPermittedException
import it.polito.wa2.g17.travelerservice.exceptions.BodyRequestException
import it.polito.wa2.g17.travelerservice.exceptions.InvalidPrincipalException
import it.polito.wa2.g17.travelerservice.services.AdminService
import it.polito.wa2.g17.travelerservice.services.CustomerService
import it.polito.wa2.g17.travelerservice.utils.AdminPermissions
import it.polito.wa2.g17.travelerservice.utils.AdminPermissionsUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@RestController
class TravelerServiceController {

    @Autowired
    lateinit var customerService: CustomerService

    @Autowired
    lateinit var adminPermissionsUtils: AdminPermissionsUtils

    @Autowired
    lateinit var adminService: AdminService

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/user/my/profile")
    @ResponseBody
    fun getUserProfile() : UserDetailsDto {

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        val profile = customerService.getProfile(authenticationUsername)

        return profile.toDTO()
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/traveler/user/my/profile")
    @ResponseBody
    fun updateUserProfile(@RequestBody @Valid userProfileDto: UserProfileDto, br: BindingResult){

        if(br.hasErrors()){
            println(br.fieldErrors.toString())
            throw BodyRequestException()
        }

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        customerService.updateProfile(authenticationUsername, userProfileDto)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/user/my/tickets")
    @ResponseBody
    fun getUserTickets() : List<TicketPurchasedDto> {

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        return customerService.getTickets(authenticationUsername)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/user/my/tickets/{orderID}")
    @ResponseBody
    fun getUserTicketsByOrderID(@PathVariable orderID: UUID) : List<TicketPurchasedDto> {

        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        return customerService.getTicketsByOrderID(authenticationUsername, orderID)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/user/my/tickets/qr/{ticketID}")
    @ResponseBody
    fun getUserTicketQRByOrderID(@PathVariable ticketID: UUID) : ResponseEntity<InputStreamResource> {
        val authenticationUsername =
            SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()

        return customerService.getTicketQRByTicketID(authenticationUsername, ticketID)
    }

//    @ResponseStatus(HttpStatus.OK)
//    @PostMapping("/traveler/user/my/tickets")
//    @ResponseBody
//    fun requireTickets(@RequestBody @Valid ticketDto: TicketDto, br: BindingResult) : MutableList<TicketPurchasedDto> {
//
//        if(br.hasErrors()){
//            throw BodyRequestException()
//        }
//
//        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()
//
//        return customerService.requireTickets(authenticationUsername, ticketDto)
//    }

//    @ResponseStatus(HttpStatus.OK)
//    @PutMapping("/traveler/super_admin/enroll")
//    @ResponseBody
//    fun enrollAdmin(@RequestBody @Valid adminProfileDto: UserProfileDto, br: BindingResult) {
//        if(br.hasErrors()){
//            println(br.fieldErrors.toString())
//            throw BodyRequestException()
//        }
//
//        val authenticationUsername = SecurityContextHolder.getContext().authentication.name ?: throw InvalidPrincipalException()
//
//        adminService.enrollAdmin(authenticationUsername, adminProfileDto)
//
//        return
//    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/admin/travelers")
    @ResponseBody
    fun getTravelers() : List<UserDetailsDto> {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TRAVELER_SERVICE_MANAGE_TRAVELERS)){
            throw AdminOperationNotPermittedException()
        }

        return adminService.getTravelers()
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/admin/traveler/{userID}/profile")
    @ResponseBody
    fun getTravelerProfile(@PathVariable userID: String) : UserDetailsDto {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TRAVELER_SERVICE_MANAGE_TRAVELERS)){
            throw AdminOperationNotPermittedException()
        }

        return adminService.getTravelerProfile(userID)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/admin/traveler/{userID}/tickets")
    @ResponseBody
    fun getTravelerTickets(@PathVariable userID: String) : List<TicketPurchasedDto> {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TRAVELER_SERVICE_MANAGE_TRAVELERS)){
            throw AdminOperationNotPermittedException()
        }

        return adminService.getTravelerTickets(userID)
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/traveler/admin/report/user")
    @ResponseBody
    fun generateReportUser(@RequestBody @Valid userReportRequestDTO: UserReportRequestDTO, br: BindingResult) : ReportResponseDTO {

        if(br.hasErrors()){
            throw BodyRequestException()
        }

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TRAVELER_SERVICE_MANAGE_REPORTS)){
            throw AdminOperationNotPermittedException()
        }

        return adminService.generateReportUser(userReportRequestDTO)
    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/traveler/admin/report")
    @ResponseBody
    fun generateReport(@RequestBody @Valid reportRequestDTO: ReportRequestDTO, br: BindingResult) : ReportResponseDTO {

        if(br.hasErrors()){
            throw BodyRequestException()
        }

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TRAVELER_SERVICE_MANAGE_REPORTS)){
            throw AdminOperationNotPermittedException()
        }

        return adminService.generateReport(reportRequestDTO)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/traveler/admin/report/{reportID}")
    @ResponseBody
    fun getReport(@PathVariable @NotEmpty reportID: UUID): ReportDTO {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TRAVELER_SERVICE_MANAGE_REPORTS)){
            throw AdminOperationNotPermittedException()
        }

        return adminService.getReport(reportID)
    }

}
