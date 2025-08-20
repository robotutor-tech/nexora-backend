package com.robotutor.nexora.modules.auth.adapters.persistance.model

import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.modules.auth.domain.model.TokenId
import com.robotutor.nexora.modules.auth.domain.model.TokenType
import com.robotutor.nexora.modules.auth.domain.model.TokenValue
import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.shared.domain.model.TokenIdentifier
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val TOKEN_COLLECTION = "tokens"

@TypeAlias("TokenDocument")
@Document(TOKEN_COLLECTION)
data class TokenDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val tokenId: String,
    @Indexed(unique = true)
    val value: String,
    val identifier: Identifier<TokenIdentifier>,
    val tokenType: TokenType,
    val issuedAt: Instant,
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    val expiresAt: Instant,
    val metadata: Map<String, Any?>,
) {
    fun toDomainModel(): Token {
        return Token(
            tokenId = TokenId(tokenId),
            tokenType = tokenType,
            value = TokenValue(value),
            identifier = identifier,
            issuedAt = issuedAt,
            expiresAt = expiresAt,
            metadata = metadata
        )
    }

    companion object {
        fun from(token: Token): TokenDocument {
            return TokenDocument(
                tokenId = token.tokenId.value,
                value = token.value.value,
                tokenType = token.tokenType,
                identifier = token.identifier,
                issuedAt = token.issuedAt,
                expiresAt = token.expiresAt,
                metadata = token.metadata,
            )
        }
    }
}
