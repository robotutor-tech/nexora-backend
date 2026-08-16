package com.robotutor.nexora.module.user.interfaces.messaging.mapper

import com.robotutor.nexora.module.user.application.command.ActivateUserCommand
import com.robotutor.nexora.module.user.application.command.CompensateUserAccountCreationCommand
import com.robotutor.nexora.module.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.module.user.interfaces.messaging.message.CompensateUserAccountCreationMessage
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.UserId

object UserEventMapper {
    fun toActivateUserCommand(eventMessage: ActivateUserMessage): ActivateUserCommand {
        return ActivateUserCommand(UserId(eventMessage.userId), AccountId(eventMessage.accountId))
    }

    fun toCompensateUserAccountCreationCommand(eventMessage: CompensateUserAccountCreationMessage): CompensateUserAccountCreationCommand {
        return CompensateUserAccountCreationCommand(UserId(eventMessage.userId))
    }
}
