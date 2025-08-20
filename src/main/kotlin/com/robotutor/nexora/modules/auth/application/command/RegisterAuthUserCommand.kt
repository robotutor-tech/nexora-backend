package com.robotutor.nexora.modules.auth.application.command

import com.robotutor.nexora.modules.auth.domain.model.Password
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.domain.model.UserId

data class RegisterAuthUserCommand(val userId: UserId, val email: Email, val password: Password)
