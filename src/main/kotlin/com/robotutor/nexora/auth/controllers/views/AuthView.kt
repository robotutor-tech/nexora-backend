package com.robotutor.nexora.auth.controllers.views

import com.robotutor.nexora.security.models.UserId
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AuthUserRequest(
    @field:NotBlank(message = "UserId is required")
    val userId: UserId,
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, message = "Password must be at least 8 characters long")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+\$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    val password: String
)

data class AuthLoginRequest(
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String
)
