package it.polito.wa2.g17.LoginService.dtos

import javax.validation.constraints.Email
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


data class RegistrationDTO (
    @NotNull
    @field:Size(min=1)
    val nickname : String,

    @Email
    @NotNull
    @field:Size(min=1)
    val email : String,

    @NotNull
    @field:Size(min=8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    val password : String
    )