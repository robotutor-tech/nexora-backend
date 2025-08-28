package com.robotutor.nexora.modules.user.application.command

import com.robotutor.nexora.modules.auth.domain.model.Password
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.Mobile
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.UserId

data class RegisterUserCommand(
    val email: Email,
    val password: Password,
    val name: Name,
    val mobile: Mobile
)

data class RegisterAuthUserCommand(
        val userId: UserId,
        val email: Email,
        val password: Password,
)

data class GetUserCommand(val userId: UserId)
