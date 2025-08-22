package com.robotutor.nexora.modules.auth.application.strategy

import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.model.TokenValue
import com.robotutor.nexora.modules.auth.domain.strategy.TokenGenerationStrategy
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import java.time.Instant
import java.util.*

class RefreshTokenStrategy : TokenGenerationStrategy {
    override fun generate(
        principalType: com.robotutor.nexora.shared.domain.model.TokenPrincipalType,
        principalContext: PrincipalContext,
        metadata: Map<String, String>
    ): Token {
        return Token(
            tokenId = TokenId(UUID.randomUUID().toString()),
            tokenType = TokenType.REFRESH,
            value = TokenValue.generate(),
            issuedAt = Instant.now(),
            expiresAt = Instant.now().plusSeconds(60 * 60 * 24 * 7),
            metadata = metadata,
            principalType = principalType,
            principal = principalContext
        )
    }
}