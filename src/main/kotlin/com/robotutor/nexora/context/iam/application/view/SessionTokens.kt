package com.robotutor.nexora.context.iam.application.view

import com.robotutor.nexora.context.iam.domain.vo.AccessToken
import com.robotutor.nexora.context.iam.domain.vo.TokenValue

data class SessionTokens(val accessToken: AccessToken, val refreshToken: TokenValue)
