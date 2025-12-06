package com.robotutor.nexora.context.iam.application.factory

import com.robotutor.nexora.context.iam.application.strategy.AuthorizationTokenStrategy
import com.robotutor.nexora.context.iam.application.strategy.RefreshTokenStrategy
import com.robotutor.nexora.context.iam.application.strategy.TokenGenerationStrategy
import com.robotutor.nexora.context.iam.domain.entity.TokenType
import org.springframework.stereotype.Service

@Service
class TokenFactory(
    private val authorizationTokenStrategy: AuthorizationTokenStrategy,
    private val refreshTokenStrategy: RefreshTokenStrategy,
) {
    fun getStrategy(type: TokenType): TokenGenerationStrategy {
        return when (type) {
            TokenType.AUTHORIZATION -> authorizationTokenStrategy
            TokenType.REFRESH -> refreshTokenStrategy
        }
    }
}