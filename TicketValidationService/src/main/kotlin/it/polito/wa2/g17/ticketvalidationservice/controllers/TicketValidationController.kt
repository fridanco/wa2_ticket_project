package it.polito.wa2.g17.ticketvalidationservice.controllers

import it.polito.wa2.g17.ticketvalidationservice.dtos.TicketDTO
import it.polito.wa2.g17.ticketvalidationservice.dtos.TicketTurnstileDTO
import it.polito.wa2.g17.ticketvalidationservice.dtos.TurnstileJwtDTO
import it.polito.wa2.g17.ticketvalidationservice.exceptions.InvalidPrincipalException
import it.polito.wa2.g17.ticketvalidationservice.exceptions.InvalidRequestBodyException
import it.polito.wa2.g17.ticketvalidationservice.exceptions.InvalidTicketException
import it.polito.wa2.g17.ticketvalidationservice.services.TicketValidationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid


@RestController
class ValidationController{

    @Autowired
    lateinit var ticketValidationService : TicketValidationService

    @PostMapping("/ticket/validation/embedded/validate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun responseTurnstileValidate(@RequestBody @Valid ticketTurnstileDTO: TicketTurnstileDTO, br : BindingResult){

        val turnstile : TurnstileJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as TurnstileJwtDTO

        //Request body validation
        if(br.hasErrors()){
            throw InvalidRequestBodyException()
        }

        try {
            ticketValidationService.validateTicket(
                TicketDTO(turnstile.id.toString(), ticketTurnstileDTO.ticketJwt, turnstile.zid)
            )
        }
        catch (exception: Exception){
            println(exception.message)
            throw InvalidTicketException()
        }
    }

    @PostMapping("/ticket/validation/user/validate")
    @ResponseStatus(HttpStatus.OK)
    fun responseValidate(@RequestBody @Valid ticketDTO: TicketDTO, br : BindingResult) {

        //Request body validation
        if(br.hasErrors()){
            throw InvalidRequestBodyException()
        }

        ticketValidationService.validateTicket(ticketDTO)

    }

}