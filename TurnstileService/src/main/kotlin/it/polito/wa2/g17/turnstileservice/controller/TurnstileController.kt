package it.polito.wa2.g17.turnstileservice.controller

import it.polito.wa2.g17.turnstileservice.dtos.*
import it.polito.wa2.g17.turnstileservice.exceptions.*
import it.polito.wa2.g17.turnstileservice.services.TurnstileService
import it.polito.wa2.g17.turnstileservice.utils.AdminPermissions
import it.polito.wa2.g17.turnstileservice.utils.AdminPermissionsUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.constraints.NotNull

@RestController
class TurnstileController {

    @Autowired
    lateinit var turnstileService: TurnstileService

    @Autowired
    lateinit var adminPermissionsUtils: AdminPermissionsUtils

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/turnstile/admin/turnstiles")
    @ResponseBody
    fun getTurnstiles(): List<TurnstileDTO> {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TURNSTILE_SERVICE_MANAGE_TURNSTILE)){
            throw AdminOperationNotPermittedException()
        }

        return turnstileService.getTurnstiles()
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/turnstile/admin/turnstile/{turnstileID}")
    @ResponseBody
    fun getTurnstiles(@PathVariable @NotNull turnstileID: Long): TurnstileDTO {

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TURNSTILE_SERVICE_MANAGE_TURNSTILE)){
            throw AdminOperationNotPermittedException()
        }

        return turnstileService.getTurnstileDTO(turnstileID)
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/turnstile/admin/add")
    @ResponseBody
    fun addTurnstile( @RequestBody turnstileAddDTO : TurnstileAddDTO, br : BindingResult) : TurnstileCreatedResponseDTO {
        if(br.hasErrors()) {
           throw InvalidRequestBodyException()
        }

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TURNSTILE_SERVICE_MANAGE_TURNSTILE)){
            throw AdminOperationNotPermittedException()
        }

        return turnstileService.addTurnstile(turnstileAddDTO)
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/turnstile/admin/{turnstile_id}/{disabled}")
    fun putStateTurnstile(@PathVariable turnstile_id : Long, @PathVariable disabled : Boolean){

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TURNSTILE_SERVICE_MANAGE_TURNSTILE)){
            throw AdminOperationNotPermittedException()
        }

        if(!turnstileService.putStateTurnstile(turnstile_id,disabled))
            throw TurnstileNotFoundException(turnstile_id)
    }
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/turnstile/admin/{turnstile_id}")
    fun deleteTurnstile ( @PathVariable turnstile_id: Long){

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.TURNSTILE_SERVICE_MANAGE_TURNSTILE)){
            throw AdminOperationNotPermittedException()
        }

        try {
            turnstileService.deleteTurnstile(turnstile_id)
        }
        catch (e:Exception){
            throw TurnstileNotDeletedException()
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/turnstile/embedded/validate")
    fun validateTicket(@RequestBody ticketTurnstileDTO: TicketTurnstileDTO, br: BindingResult){
        if(br.hasErrors()){
            throw InvalidRequestBodyException()
        }

        val turnstile : TurnstileJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as TurnstileJwtDTO

        if(!turnstileService.validateTicketTurnstile(ticketTurnstileDTO, turnstile)){
            throw TurnstileNotFoundException(turnstile.id)
        }
    }







}