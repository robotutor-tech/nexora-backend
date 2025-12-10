package com.robotutor.nexora.context.iam.application.view

import com.robotutor.nexora.context.iam.domain.vo.TokenValue

data class SessionTokens(val accessToken: TokenValue, val refreshToken: TokenValue)
