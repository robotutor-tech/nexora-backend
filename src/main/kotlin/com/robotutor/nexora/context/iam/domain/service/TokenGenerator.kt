package com.robotutor.nexora.context.iam.domain.service

import com.robotutor.nexora.context.iam.domain.vo.AccessToken
import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal
import com.robotutor.nexora.context.iam.domain.vo.TokenPayload
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import java.time.Instant

interface TokenGenerator {
    fun generateAccessToken(
        sessionPrincipal: SessionPrincipal,
        expiresAt: Instant = Instant.now().plusSeconds(3600)
    ): AccessToken

    fun validateAccessToken(tokenValue: TokenValue): TokenPayload
}