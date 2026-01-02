package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.module.iam.domain.vo.TokenValue

data class ValidateTokenCommand(val tokenValue: TokenValue)
