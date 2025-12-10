package com.robotutor.nexora.context.iam.domain.service

import com.robotutor.nexora.context.iam.domain.vo.TokenPayload
import com.robotutor.nexora.context.iam.domain.vo.TokenValue

interface TokenGenerator {
    fun generateAccessToken(payload: TokenPayload): TokenValue
    fun validateAccessToken(tokenValue: TokenValue): TokenPayload
}