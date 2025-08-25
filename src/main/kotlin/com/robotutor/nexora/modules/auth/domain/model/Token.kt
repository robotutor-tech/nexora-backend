package com.robotutor.nexora.modules.auth.domain.model

import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.DomainModel
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import java.time.Instant
import java.util.UUID

data class Token(
    val tokenId: TokenId,
    val principalType: TokenPrincipalType,
    val principal: PrincipalContext,
    val tokenType: TokenType,
    val value: TokenValue,
    val issuedAt: Instant,
    var expiresAt: Instant,
    val metadata: Map<String, Any> = emptyMap()
) : DomainAggregate() {
    fun invalidate(): Token {
        this.expiresAt = Instant.now()
//        this.addDomainEvent() // invalidate token event
        return this
    }

    companion object {
        fun generate(
            tokenType: TokenType,
            expiresAt: Instant,
            principalType: TokenPrincipalType,
            principal: PrincipalContext,
            metadata: Map<String, String>
        ): Token {
            return Token(
                tokenId = TokenId(UUID.randomUUID().toString()),
                tokenType = tokenType,
                value = TokenValue.generate(),
                issuedAt = Instant.now(),
                expiresAt = expiresAt,
                principalType = principalType,
                principal = principal,
                metadata = metadata
            )
//            token.addDomainEvent()
//            return token
        }
    }
}

data class TokenId(val value: String)
data class TokenValue(val value: String) {
    companion object {
        fun generate(length: Int = 120): TokenValue {
            val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + "_-".split("")
            val token = List(length) { chars.random() }.joinToString("").substring(0)
            val fullToken = token + Instant.now().epochSecond.toString()
            return TokenValue(fullToken.substring(fullToken.length - length))
        }
    }
}

data class Tokens(val token: Token, val refreshToken: Token)

enum class TokenType {
    AUTHORIZATION, REFRESH
}
