package com.robotutor.nexora.modules.user.interfaces.controller.dto

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
        @field:Email(message = "Email should be valid") val email: String,
        @field:NotBlank(message = "Mobile number is required")
        @field:Pattern(
                regexp = "^[0-9]{10}$",
                message = "Mobile number should be a valid 10-digit Indian number"
        )
        val mobile: String,
        @field:NotBlank(message = "Password is required")
        @field:Size(min = 8, message = "Password must be at least 8 characters long")
        @field:Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+\$",
                message =
                        "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
        )
        val password: String
)
