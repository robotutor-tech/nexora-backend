package com.robotutor.nexora.module.iam.application.command

import com.robotutor.nexora.module.iam.domain.vo.TokenValue

data class RefreshSessionCommand(val token: TokenValue)
