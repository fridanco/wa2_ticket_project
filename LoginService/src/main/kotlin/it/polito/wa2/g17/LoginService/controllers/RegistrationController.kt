package it.polito.wa2.g17.LoginService.controllers

import it.polito.wa2.g17.LoginService.dtos.*
import it.polito.wa2.g17.LoginService.exceptions.AdminOperationNotPermittedException
import it.polito.wa2.g17.LoginService.exceptions.BodyRequestException
import it.polito.wa2.g17.LoginService.exceptions.InvalidPrincipalException
import it.polito.wa2.g17.LoginService.services.UserService
import it.polito.wa2.g17.LoginService.utils.AdminPermissions
import it.polito.wa2.g17.LoginService.utils.AdminPermissionsUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class RegistrationController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var adminPermissionsUtils: AdminPermissionsUtils

    @PostMapping("/auth/public/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    fun userRegistration(@Valid @RequestBody registrationDTO: RegistrationDTO, br : BindingResult) : RegistrationResponseDTO{

        if(br.hasErrors()){
            throw BodyRequestException()
        }
//        if(patternMatches(registrationDTO.email) == false)
//            throw BodyRequestException()
//
//        var checkDigit = false
//        var checkUppercase = false
//        var checkLowercase = false
//        var checkSymbol = false
//
//        registrationDTO.password.forEach {
//            if(it.isDigit() ){
//                checkDigit = true
//            }
//            else if( it.isLowerCase() ){
//                checkLowercase = true
//            }
//            else if ( it.isUpperCase()){
//                checkUppercase = true
//            }
//            else if (it.isWhitespace()){
//                throw BodyRequestException()
//            }
//            else {checkSymbol = true}
//        }
//        if(!checkDigit || !checkUppercase ||  !checkLowercase ||  !checkSymbol){
//            throw BodyRequestException()
//        }
        return userService.userRegistration(registrationDTO)
    }

    @PostMapping("/auth/public/validate")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun userValidation(@Valid @RequestBody validationDTO: ValidationDTO, br : BindingResult) : ValidationResponseDTO{
        if(br.hasErrors()){
            throw BodyRequestException()
        }
        return userService.userValidation(validationDTO)
    }

    @PostMapping("/auth/admin/create/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    fun adminCreateUserAccount(@Valid @RequestBody registrationDTO: RegistrationDTO, br : BindingResult): ValidationResponseDTO {

        if(br.hasErrors()){
            throw BodyRequestException()
        }

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.LOGIN_SERVICE_MANAGE_USERS)){
            throw AdminOperationNotPermittedException()
        }

        return userService.adminCreateUserAccount(registrationDTO)

    }


    @PostMapping("/auth/admin/create/admin")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    fun adminCreateAdminAccount(@Valid @RequestBody adminCreateAccountDTO: AdminCreateAccountDTO, br : BindingResult): ValidationResponseDTO {

        if(br.hasErrors()){
            throw BodyRequestException()
        }

        val adminJwt : UserJwtDTO = (SecurityContextHolder.getContext().authentication.principal ?: throw InvalidPrincipalException()) as UserJwtDTO

        if(!adminPermissionsUtils.adminHasPermission(adminJwt.permissions, AdminPermissions.LOGIN_SERVICE_MANAGE_ADMINS)){
            throw AdminOperationNotPermittedException()
        }

        return userService.adminCreateAdminAccount(adminCreateAccountDTO)
    }

}