package com.robotutor.nexora.modules.auth.application.factory

import com.robotutor.nexora.modules.auth.application.strategy.AuthorizationTokenStrategy
import com.robotutor.nexora.modules.auth.application.strategy.RefreshTokenStrategy
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.strategy.TokenGenerationStrategy

class TokenFactory() {
    fun getStrategy(type: TokenType): TokenGenerationStrategy {
        return when (type) {
            TokenType.AUTHORIZATION -> AuthorizationTokenStrategy()
            TokenType.REFRESH -> RefreshTokenStrategy()
            else -> throw IllegalArgumentException("Invalid token type")
        }
    }
}