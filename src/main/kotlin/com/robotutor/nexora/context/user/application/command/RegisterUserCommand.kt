package com.robotutor.nexora.context.user.application.command

import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.Mobile
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.AccountId

data class RegisterUserCommand(
    val accountId: AccountId,
    val email: Email,
    val name: Name,
    val mobile: Mobile,
) : Command

data class GetUserQuery(val accountId: AccountId) : Command
