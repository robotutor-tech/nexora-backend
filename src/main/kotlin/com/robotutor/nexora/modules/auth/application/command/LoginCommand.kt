package com.robotutor.nexora.modules.auth.application.command

import com.robotutor.nexora.modules.auth.domain.model.Email
import com.robotutor.nexora.modules.auth.domain.model.Password

data class LoginCommand(val email: Email, val password: Password)
