package it.polito.wa2.g17.LoginService.dtos

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class CredentialDTO(
    @NotNull
    @field:Size(min=1)
    val nickname : String,

    @NotNull
    @field:Size(min=8)
    val password : String)