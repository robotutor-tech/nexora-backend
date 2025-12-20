package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountId

data class ActivateAccountCommand(val accountId: AccountId) : Command
