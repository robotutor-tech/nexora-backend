package com.robotutor.nexora.context.user.interfaces.messaging.mapper

import com.robotutor.nexora.context.user.application.command.ActivateUserCommand
import com.robotutor.nexora.context.user.application.command.CompensateUserCommand
import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.context.user.interfaces.messaging.message.ActivateUserMessage
import com.robotutor.nexora.context.user.interfaces.messaging.message.CompensateUserMessage

object UserEventMapper {
    fun toActivateUserCommand(eventMessage: ActivateUserMessage): ActivateUserCommand {
        return ActivateUserCommand(UserId(eventMessage.userId))
    }

    fun toCompensateUserCommand(eventMessage: CompensateUserMessage): CompensateUserCommand {
        return CompensateUserCommand(UserId(eventMessage.userId))
    }
}