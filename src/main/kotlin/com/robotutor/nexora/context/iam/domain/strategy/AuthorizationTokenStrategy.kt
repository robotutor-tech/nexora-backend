package com.robotutor.nexora.context.iam.domain.strategy

import com.robotutor.nexora.context.iam.domain.aggregate.TokenAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType
import com.robotutor.nexora.context.iam.domain.aggregate.TokenType
import com.robotutor.nexora.context.iam.domain.vo.TokenPrincipalContext
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthorizationTokenStrategy() : TokenGenerationStrategy {
    override fun generate(principalType: TokenPrincipalType, principalContext: TokenPrincipalContext): TokenAggregate {
        return TokenAggregate.generate(
            tokenType = TokenType.AUTHORIZATION,
            expiresAt = getExpiresAt(principalType),
            principalType = principalType,
            principal = principalContext,
        )
    }

    private fun getExpiresAt(principalType: TokenPrincipalType): Instant {
        val seconds: Long = when (principalType) {
            TokenPrincipalType.ACCOUNT -> 60 * 60
            TokenPrincipalType.ACTOR -> 6 * 60 * 60
        }
        return Instant.now().plusSeconds(seconds)
    }
}