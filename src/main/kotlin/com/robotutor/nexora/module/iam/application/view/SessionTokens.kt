package com.robotutor.nexora.module.iam.application.view

import com.robotutor.nexora.module.iam.domain.vo.AccessToken
import com.robotutor.nexora.module.iam.domain.vo.TokenValue

data class SessionTokens(val accessToken: AccessToken, val refreshToken: TokenValue)
