package it.polito.wa2.g17.turnstileservice.controller

import it.polito.wa2.g17.turnstileservice.dtos.ErrorMessageDTO
import it.polito.wa2.g17.turnstileservice.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ControllerAdvisor {


    @ExceptionHandler(TurnstileDisabledException::class)
    fun handleTurnstileDisabledException(e: TurnstileDisabledException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.SERVICE_UNAVAILABLE)
    }
    @ExceptionHandler(TurnstileNotFoundException::class)
    fun handleTurnstileNotFoundException(e:TurnstileNotFoundException) : ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.NOT_ACCEPTABLE)
    }

    @ExceptionHandler(InvalidRequestBodyException::class)
    fun handleInvalidRequestBodyException(e: InvalidRequestBodyException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidAuthorizationHeader::class)
    fun handleInvalidAuthorizationHeader(e: InvalidAuthorizationHeader) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidPrincipalException::class)
    fun handleInvalidPrincipalException(e: InvalidPrincipalException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("Malformed authentication token"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler( TurnstileNotDeletedException::class)
    fun handleTurnstileNotDeletedException(e: TurnstileNotDeletedException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(NetworkCallFailedException::class)
    fun handleNetworkCallFailedException(e: NetworkCallFailedException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.FAILED_DEPENDENCY)
    }
    @ExceptionHandler(TicketValidationFailedException::class)
    fun handleTicketValidationFailedException(e: TicketValidationFailedException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.FAILED_DEPENDENCY)
    }

    @ExceptionHandler(AdminOperationNotPermittedException::class)
    fun handleAdminOperationNotPermittedException(e: AdminOperationNotPermittedException): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.UNAUTHORIZED)
    }

}