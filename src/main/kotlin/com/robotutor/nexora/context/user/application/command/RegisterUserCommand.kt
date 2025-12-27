package com.robotutor.nexora.context.user.application.command

import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name

data class RegisterUserCommand(val email: Email, val name: Name, val mobile: Mobile) : Command
