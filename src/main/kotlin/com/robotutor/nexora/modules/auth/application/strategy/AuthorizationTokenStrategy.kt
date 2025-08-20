package com.robotutor.nexora.modules.auth.application.strategy

import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.model.TokenValue
import com.robotutor.nexora.modules.auth.domain.strategy.TokenGenerationStrategy
import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.shared.domain.model.TokenIdentifier
import java.time.Instant
import java.util.UUID

class AuthorizationTokenStrategy() : TokenGenerationStrategy {
    override fun generate(identifier: Identifier<TokenIdentifier>, metadata: Map<String, Any?>?): Token {
        return Token(
            tokenId = TokenId(UUID.randomUUID().toString()),
            identifier = identifier,
            tokenType = TokenType.AUTHORIZATION,
            value = TokenValue.generate(),
            issuedAt = Instant.now(),
            expiresAt = Instant.now().plusSeconds(60 * 60),
            metadata = metadata ?: emptyMap()
        )
    }
}