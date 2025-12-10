package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.context.iam.domain.vo.TokenValue

data class RefreshSessionCommand(val token: TokenValue)
