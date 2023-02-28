package it.polito.wa2.g17.LoginService.controllers

import it.polito.wa2.g17.LoginService.dtos.ErrorMessageDTO
import it.polito.wa2.g17.LoginService.exceptions.*
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
    @ExceptionHandler(TooManyRequestException::class)
        fun handleTooManyRequestException(e:TooManyRequestException ) : ResponseEntity<Any>{
            println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.TOO_MANY_REQUESTS)
    }
    @ExceptionHandler(DeadlineExpiredException::class)
        fun handleDeadlineExpiredException(e: DeadlineExpiredException): ResponseEntity<Any>{
            println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.NOT_FOUND)
        }
    @ExceptionHandler(ActivationCodeNotMatchException::class)
        fun handleActivationCodeNotMatchException(e:ActivationCodeNotMatchException) : ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.NOT_FOUND)
        }
    @ExceptionHandler(ProvisionalIdNotExist::class)
        fun handleProvisionalIdNotExist(e:ProvisionalIdNotExist) : ResponseEntity<Any>{
            println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.NOT_FOUND)
        }
    @ExceptionHandler(UserIsNotUnique::class)
    fun handleUserIsNotUnique(e:UserIsNotUnique): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(UserDoesNotExistException::class)
    fun handleUserDoesNotExist(e:UserDoesNotExistException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.UNAUTHORIZED)
    }
    @ExceptionHandler(PasswordDoesNotMatch::class)
    fun handlePasswordDoesNotMatch(e:PasswordDoesNotMatch):ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"),HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(UserRoleDoesNotExistException::class)
    fun handleUserRoleDoesNotExistException(e: UserRoleDoesNotExistException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccountNotValidatedException::class)
    fun handleAccountNotValidatedException(e: AccountNotValidatedException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccountDisabledException::class)
    fun handleAccountDisabledException(e: AccountDisabledException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AdminOperationNotPermittedException::class)
    fun handleAdminOperationNotPermittedException(e: AdminOperationNotPermittedException): ResponseEntity<Any>{
        println(e.message)
        return ResponseEntity(ErrorMessageDTO("${e.message}"), HttpStatus.UNAUTHORIZED)
    }

}