package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.Email
import com.robotutor.nexora.module.identity.domain.vo.RawPassword
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.FullName
import com.robotutor.nexora.shared.domain.vo.Mobile

data class RegisterUserAccountCommand(
    val name: FullName,
    val mobile: Mobile,
    val email: Email,
    val password: RawPassword,
) : Command
