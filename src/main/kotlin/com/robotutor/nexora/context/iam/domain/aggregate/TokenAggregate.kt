package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.vo.TokenId
import com.robotutor.nexora.context.iam.domain.vo.TokenPrincipalContext
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.shared.domain.AggregateRoot
import java.time.Instant

data class TokenAggregate(
    val tokenId: TokenId,
    val principalType: TokenPrincipalType,
    val principal: TokenPrincipalContext,
    val tokenType: TokenType,
    val value: TokenValue,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val otherTokenId: TokenId? = null,
    val version: Long? = null
) : AggregateRoot<TokenAggregate, TokenId, IAMEvent>(tokenId) {
    companion object {
        fun generate(
            tokenType: TokenType,
            expiresAt: Instant,
            principalType: TokenPrincipalType,
            principal: TokenPrincipalContext
        ): TokenAggregate {
            return TokenAggregate(
                tokenId = TokenId.generate(),
                tokenType = tokenType,
                value = TokenValue.generate(),
                issuedAt = Instant.now(),
                expiresAt = expiresAt,
                principalType = principalType,
                principal = principal,
            )
        }
    }

    fun updateOtherTokenId(tokenId: TokenId): TokenAggregate {
        return this.copy(otherTokenId = tokenId)
    }
}

enum class TokenType {
    AUTHORIZATION, REFRESH, INVITATION,
}

enum class TokenPrincipalType {
    ACCOUNT,
    ACTOR,
}