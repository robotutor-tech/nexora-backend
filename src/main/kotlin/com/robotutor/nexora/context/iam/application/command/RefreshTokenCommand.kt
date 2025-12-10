package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.TokenValue

data class RefreshTokensCommand(val token: TokenValue)
