package com.robotutor.nexora.module.user.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.module.user.domain.vo.Email
import com.robotutor.nexora.module.user.domain.vo.Mobile
import com.robotutor.nexora.shared.domain.vo.FullName

data class RegisterUserCommand(val email: Email, val fullName: FullName, val mobile: Mobile) : Command {
    fun toMetaData(): Map<String, Any?> {
        return mapOf("email" to email.value, "name" to fullName.value, "mobile" to mobile.value)
    }
}
