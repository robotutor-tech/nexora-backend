package com.robotutor.nexora.context.iam.application.strategy

import com.robotutor.nexora.context.iam.domain.entity.Token
import com.robotutor.nexora.context.iam.domain.entity.TokenPrincipalType
import com.robotutor.nexora.context.iam.domain.entity.TokenType
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.DeviceContext
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.UserContext
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class RefreshTokenStrategy : TokenGenerationStrategy {
    override fun generate(principalType: TokenPrincipalType, principalContext: PrincipalContext): Token {
        return Token.generate(
            tokenType = TokenType.REFRESH,
            expiresAt = getExpiresAt(principalType, principalContext),
            principalType = principalType,
            principal = principalContext
        )
    }

    private fun getExpiresAt(principalType: TokenPrincipalType, principalContext: PrincipalContext): Instant {
        val seconds: Long = when (principalType) {
            TokenPrincipalType.USER -> 7 * 24 * 60 * 60
            TokenPrincipalType.INVITATION -> 0
            TokenPrincipalType.INTERNAL -> 0
            TokenPrincipalType.ACTOR -> {
                when ((principalContext as ActorContext).principalContext) {
                    is DeviceContext -> 30 * 24 * 60 * 60
                    is UserContext -> 7 * 24 * 60 * 60
                }
            }
        }
        return Instant.now().plusSeconds(seconds)
    }
}