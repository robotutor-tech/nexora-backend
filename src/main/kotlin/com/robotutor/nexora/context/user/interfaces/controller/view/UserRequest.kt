package com.robotutor.nexora.context.user.interfaces.controller.view

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(
        min = 2,
        max = 30,
        message = "Name should not be less than 2 char or more than 30 char"
    )
    val name: String,
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Mobile number is required")
    @field:Pattern(
        regexp = "^[0-9]{10}$",
        message = "Mobile number should be a valid 10-digit Indian number"
    )
    val mobile: String,
)
