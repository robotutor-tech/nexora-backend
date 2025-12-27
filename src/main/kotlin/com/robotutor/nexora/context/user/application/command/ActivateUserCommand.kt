package com.robotutor.nexora.context.user.application.command

import com.robotutor.nexora.context.user.domain.vo.UserId
import com.robotutor.nexora.shared.application.command.Command

data class ActivateUserCommand(val userId: UserId) : Command


