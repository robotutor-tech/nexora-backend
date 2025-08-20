package com.robotutor.nexora.modules.auth.application.command

import com.robotutor.nexora.modules.auth.domain.model.Password
import com.robotutor.nexora.shared.domain.model.Email

data class LoginCommand(val email: Email, val password: Password)
