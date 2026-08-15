package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.module.identity.domain.vo.Email
import com.robotutor.nexora.module.identity.domain.vo.RawPassword
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.UserId

data class RegisterUserAccountCommand(
    val email: Email,
    val password: RawPassword,
    val userId: UserId,
) : Command
