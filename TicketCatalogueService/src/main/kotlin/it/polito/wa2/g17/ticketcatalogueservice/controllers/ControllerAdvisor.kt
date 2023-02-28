package it.polito.wa2.g17.ticketcatalogueservice.controllers

import it.polito.wa2.g17.ticketcatalogueservice.dtos.ErrorMessageDTO
import it.polito.wa2.g17.ticketcatalogueservice.exceptions.*
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
    @ExceptionHandler(KafkaMessageException::class)
    fun handleKafkaMessageException(e:KafkaMessageException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(InvalidAuthorizationHeader::class)
    fun handleKafkaMessageException(e:InvalidAuthorizationHeader):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(InvalidJwtException::class)
    fun handleKafkaMessageException(e:InvalidJwtException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(InvalidPrincipalException::class)
    fun handleKafkaMessageException(e:InvalidPrincipalException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(OrderNotFoundException::class)
    fun handleKafkaMessageException(e:OrderNotFoundException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(TicketOrderNotFoundException::class)
    fun handleKafkaMessageException(e:TicketOrderNotFoundException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.NOT_FOUND)
    }
    @ExceptionHandler(TicketPurchaseNotPermittedException::class)
    fun handleKafkaMessageException(e:TicketPurchaseNotPermittedException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.FORBIDDEN)
    }
    @ExceptionHandler(TicketTypeCouldNotBeInsertedException::class)
    fun handleKafkaMessageException(e:TicketTypeCouldNotBeInsertedException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(TicketTypeNotFoundException::class)
    fun handleKafkaMessageException(e:TicketTypeNotFoundException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(UserDetailsCouldNotBeRetrievedException::class)
    fun handleKafkaMessageException(e:UserDetailsCouldNotBeRetrievedException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(UserEmptyProfileException::class)
    fun handleKafkaMessageException(e:UserEmptyProfileException):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AdminOperationNotPermittedException::class)
    fun handleAdminOperationNotPermittedException(e: AdminOperationNotPermittedException): ResponseEntity<Any> {
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.UNAUTHORIZED)
    }

}