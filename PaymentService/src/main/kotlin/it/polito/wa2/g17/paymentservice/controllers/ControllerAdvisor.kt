package it.polito.wa2.g17.paymentservice.controllers

import it.polito.wa2.g17.paymentservice.dtos.ErrorMessageDTO
import it.polito.wa2.g17.paymentservice.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvisor {
    @ExceptionHandler(BodyRequestException::class)
    fun handleBodyRequestException(e: BodyRequestException): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidAuthorizationHeader::class)
    fun handleBodyRequestException(e: InvalidAuthorizationHeader): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidJwtException::class)
    fun handleBodyRequestException(e: InvalidJwtException): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidPrincipalException::class)
    fun handleBodyRequestException(e: InvalidPrincipalException): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AdminOperationNotPermittedException::class)
    fun handleAdminOperationNotPermittedException(e: AdminOperationNotPermittedException): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.UNAUTHORIZED)
    }


}