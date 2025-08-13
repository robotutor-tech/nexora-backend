package com.robotutor.nexora.modules.user.application.command

import com.robotutor.nexora.modules.user.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId


data class RegisterUserCommand(
    val email: String,
    val password: String,
    val name: String
)

data class RegisterAuthUserCommand(
    val userId: UserId,
    val email: Email,
    val password: String,
)
