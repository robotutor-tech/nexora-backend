package com.robotutor.nexora.modules.user.adapters.inbound.conroller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(min = 2, max = 30, message = "Name should not be less than 2 char or more than 30 char")
    val name: String,
    @field:Email(message = "Email should be valid")
    val email: String,
)
