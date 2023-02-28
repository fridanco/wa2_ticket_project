package it.polito.wa2.g17.travelerservice.dtos

import org.springframework.format.annotation.DateTimeFormat
import javax.validation.constraints.NotNull

data class UserReportRequestDTO (
    @NotNull
    val userID: String,
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    val startTime: String,
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    val endTime: String
    )