package com.robotutor.nexora.context.iam.interfaces.messaging.mapper

import com.robotutor.nexora.context.iam.application.command.ActivateAccountCommand
import com.robotutor.nexora.context.iam.application.command.CompensateAccountCommand
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountActivatedMessage
import com.robotutor.nexora.context.iam.interfaces.messaging.message.AccountRegistrationFailedEventMessage
import com.robotutor.nexora.shared.domain.vo.AccountId

object IAMMapper {
    fun toAccountActivatedCommand(eventMessage: AccountActivatedMessage): ActivateAccountCommand {
        return ActivateAccountCommand(accountId = AccountId(eventMessage.accountId))
    }

    fun toCompensateAccountCommand(eventMessage: AccountRegistrationFailedEventMessage): CompensateAccountCommand {
        return CompensateAccountCommand(accountId = AccountId(eventMessage.accountId))
    }
}