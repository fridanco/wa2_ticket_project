package it.polito.wa2.g17.LoginService.dtos

import java.util.*
import javax.validation.constraints.NotNull

data class ValidationDTO (
    @NotNull
    val provisionalId : UUID,

    @NotNull
    val activationCode : UUID
    )