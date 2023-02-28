package it.polito.wa2.g17.travelerservice.controllers

import it.polito.wa2.g17.travelerservice.dtos.ErrorMessageDTO
import it.polito.wa2.g17.travelerservice.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ControllerAdvisor {

    @ExceptionHandler(BodyRequestException::class)
    fun handleBodyRequestException(e: BodyRequestException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidAuthorizationHeader::class)
    fun handleInvalidAuthorizationHeader(e: InvalidAuthorizationHeader): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidJwtException::class)
        fun handleInvalidJwtException(e:InvalidJwtException) : ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.UNAUTHORIZED)
        }

    @ExceptionHandler(InvalidPrincipalException::class)
        fun handleInvalidPrincipalException(e:InvalidPrincipalException) : ResponseEntity<Any>{
            println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.UNAUTHORIZED)
        }

    @ExceptionHandler(UserEmptyProfileException::class)
    fun handleUserEmptyProfileException(e:UserEmptyProfileException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("User profile is empty, please create a profile first"), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(ReportNotFoundException::class)
    fun handleReportNotFoundException(e:ReportNotFoundException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AdminOperationNotPermittedException::class)
    fun handleAdminOperationNotPermittedException(e: AdminOperationNotPermittedException): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.UNAUTHORIZED)
    }

}