package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.TokenValue

data class ValidateTokenCommand(val tokenValue: TokenValue)
