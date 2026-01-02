package com.robotutor.nexora.module.user.application.command

import com.robotutor.nexora.module.user.domain.vo.UserId
import com.robotutor.nexora.shared.application.command.Command

data class ActivateUserCommand(val userId: UserId) : Command


