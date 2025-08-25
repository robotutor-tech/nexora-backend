package com.robotutor.nexora.modules.auth.application.factory

import com.robotutor.nexora.modules.auth.application.strategy.AuthorizationTokenStrategy
import com.robotutor.nexora.modules.auth.application.strategy.RefreshTokenStrategy
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.application.strategy.TokenGenerationStrategy
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