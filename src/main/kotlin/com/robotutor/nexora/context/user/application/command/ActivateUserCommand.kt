package com.robotutor.nexora.context.user.application.command

import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.application.command.Command

data class ActivateUserCommand(val email: Email, val accountId: AccountId) : Command
data class CompensateUserRegistrationCommand(val userId: UserId) : Command


