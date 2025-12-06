package com.robotutor.nexora.context.user.interfaces.messaging.mapper

import com.robotutor.nexora.context.user.application.command.ActivateUserCommand
import com.robotutor.nexora.context.user.application.command.CompensateUserRegistrationCommand
import com.robotutor.nexora.context.user.domain.vo.Email
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.context.user.interfaces.messaging.message.CompensateUserRegistrationMessage
import com.robotutor.nexora.context.user.interfaces.messaging.message.UserAccountCreatedMessage
import com.robotutor.nexora.shared.domain.vo.AccountId

object UserEventMapper {
    fun toActivateUserCommand(eventMessage: UserAccountCreatedMessage): ActivateUserCommand {
        return ActivateUserCommand(
            email = Email(eventMessage.credentialId),
            accountId = AccountId(eventMessage.accountId),
        )
    }

    fun toCompensateUserRegistrationCommand(eventMessage: CompensateUserRegistrationMessage): CompensateUserRegistrationCommand {
        return CompensateUserRegistrationCommand(UserId(eventMessage.userId))
    }

}
