package it.polito.wa2.g17.LoginService.controllers

import it.polito.wa2.g17.LoginService.dtos.CredentialDTO
import it.polito.wa2.g17.LoginService.exceptions.BodyRequestException
import it.polito.wa2.g17.LoginService.services.LoginService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
class LoginController {

    @Autowired
    lateinit var loginService: LoginService

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("auth/public/login")
    @ResponseBody
    fun userLogin(@Valid @RequestBody credentialDTO: CredentialDTO, br: BindingResult) : Any? {
        if(br.hasErrors()){
            throw BodyRequestException()
        }
        return loginService.loginUser(credentialDTO)
    }

}