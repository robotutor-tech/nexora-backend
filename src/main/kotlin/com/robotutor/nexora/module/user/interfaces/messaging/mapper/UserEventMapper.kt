package com.robotutor.nexora.module.user.interfaces.messaging.mapper

import com.robotutor.nexora.module.user.application.command.ActivateUserCommand
import com.robotutor.nexora.module.user.application.command.CompensateUserCommand
import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.module.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.module.user.interfaces.messaging.message.CompensateUserMessage

object UserEventMapper {
    fun toActivateUserCommand(eventMessage: ActivateUserMessage): ActivateUserCommand {
        return ActivateUserCommand(UserId(eventMessage.userId))
    }

    fun toCompensateUserCommand(eventMessage: CompensateUserMessage): CompensateUserCommand {
        return CompensateUserCommand(UserId(eventMessage.userId))
    }
}