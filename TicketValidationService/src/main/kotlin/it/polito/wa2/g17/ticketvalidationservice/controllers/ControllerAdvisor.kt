package it.polito.wa2.g17.ticketvalidationservice.controllers

import io.jsonwebtoken.JwtException
import it.polito.wa2.g17.ticketvalidationservice.dtos.ErrorMessageDTO
import it.polito.wa2.g17.ticketvalidationservice.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvisor {

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
        return ResponseEntity(ErrorMessageDTO("Authentication token is malformed"),HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(e: JwtException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJWTException(e: ExpiredJwtException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidZoneException::class)
    fun handleInvalidZoneException(e: InvalidZoneException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DuplicateTicketException::class)
    fun handleDuplicateTicketException(e: DuplicateTicketException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(TicketNotYetValidException::class)
    fun handleTicketNotYetValidException(e: TicketNotYetValidException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(InvalidTicketException::class)
    fun handleInvalidTicketException(e: InvalidTicketException) : ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.NOT_ACCEPTABLE)
    }
}