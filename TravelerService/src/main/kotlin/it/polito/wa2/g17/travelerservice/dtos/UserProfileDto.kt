package it.polito.wa2.g17.travelerservice.dtos

import org.springframework.format.annotation.DateTimeFormat
import java.util.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class UserProfileDto(
    @NotNull
    @field:Size(min = 1)
    var name: String,

    @NotNull
    @field:Size(min = 1)
    var address: String,

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    var dateOfBirth: Date,

    @NotNull
    @field:Size(min = 1)
    var telephoneNumber: String
)
