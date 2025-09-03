package com.robotutor.nexora.modules.auth.application.strategy

import com.robotutor.nexora.modules.auth.domain.entity.Token
import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.modules.auth.domain.entity.TokenType
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthorizationTokenStrategy() : TokenGenerationStrategy {
    override fun generate(principalType: TokenPrincipalType, principalContext: PrincipalContext): Token {
        return Token.generate(
            tokenType = TokenType.AUTHORIZATION,
            expiresAt = getExpiresAt(principalType, principalContext),
            principalType = principalType,
            principal = principalContext,
        )
    }

    private fun getExpiresAt(principalType: TokenPrincipalType, principalContext: PrincipalContext): Instant {
        val seconds: Long = when (principalType) {
            TokenPrincipalType.USER -> 15 * 60
            TokenPrincipalType.INVITATION -> 6 * 60 * 60
            TokenPrincipalType.INTERNAL -> 60
            TokenPrincipalType.ACTOR -> {
                when ((principalContext as ActorContext).principalContext) {
                    is DeviceContext -> 24 * 60 * 60
                    is UserContext -> 15 * 60
                }
            }
        }
        return Instant.now().plusSeconds(seconds)
    }
}