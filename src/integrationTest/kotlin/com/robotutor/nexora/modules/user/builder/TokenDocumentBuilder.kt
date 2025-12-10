package com.robotutor.nexora.modules.user.builder

import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.modules.auth.domain.entity.TokenType
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.TokenDocument
import com.robotutor.nexora.shared.infrastructure.persistence.document.PrincipalDocument
import com.robotutor.nexora.shared.infrastructure.persistence.document.UserDocument
import java.time.Instant

data class TokenDocumentBuilder(
    val tokenId: String = "tokenId",
    val value: String = "value",
    val otherToken: String? = null,
    val principalType: TokenPrincipalType = TokenPrincipalType.USER,
    val principal: PrincipalDocument = UserDocument("userId"),
    val tokenType: TokenType = TokenType.AUTHORIZATION,
    val issuedAt: Instant = Instant.parse("2023-01-01T00:00:00Z"),
    val expiresAt: Instant = Instant.parse("2023-02-01T00:00:00Z"),
    val version: Long = 0
) {
    fun build(): TokenDocument {
        return TokenDocument(
            tokenId = tokenId,
            value = value,
            otherToken = otherToken,
            principalType = principalType,
            principal = principal,
            tokenType = tokenType,
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            version = version,
        )
    }
}
