package com.robotutor.nexora.module.user.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.UserId

data class ActivateUserCommand(val userId: UserId) : Command


