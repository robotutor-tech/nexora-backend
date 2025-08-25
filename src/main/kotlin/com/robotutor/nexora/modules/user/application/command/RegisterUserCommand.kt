package com.robotutor.nexora.modules.user.application.command

import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId

data class RegisterUserCommand(
    val email: Email,
    val password: String,
    val name: Name
)

data class RegisterAuthUserCommand(
    val userId: UserId,
    val email: Email,
    val password: String,
)

data class GetUserCommand(val userId: UserId)