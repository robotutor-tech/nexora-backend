package com.robotutor.nexora.context.iam.domain.factory

import com.robotutor.nexora.context.iam.domain.strategy.AuthorizationTokenStrategy
import com.robotutor.nexora.context.iam.domain.strategy.RefreshTokenStrategy
import com.robotutor.nexora.context.iam.domain.strategy.TokenGenerationStrategy
import com.robotutor.nexora.context.iam.domain.aggregate.TokenType
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
            TokenType.INVITATION -> TODO()
        }
    }
}