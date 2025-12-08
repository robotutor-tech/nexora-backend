package com.robotutor.nexora.context.iam.infrastructure.persistence.document

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.context.iam.domain.aggregate.TokenAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType
import com.robotutor.nexora.context.iam.domain.aggregate.TokenType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val TOKEN_COLLECTION = "tokens"

@TypeAlias("Token")
@Document(TOKEN_COLLECTION)
data class TokenDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val tokenId: String,
    @Indexed(unique = true)
    val value: String,
    val otherToken: String?,
    val principalType: TokenPrincipalType,
    val principal: TokenPrincipalDocument,
    val tokenType: TokenType,
    val issuedAt: Instant,
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    val expiresAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<TokenAggregate>

sealed interface TokenPrincipalDocument
data class AccountTokenPrincipalDocument(
    val accountId: String,
    val type: AccountType
) : TokenPrincipalDocument

data class ActorTokenPrincipalDocument(
    val actorId: String,
    val roleId: String,
) : TokenPrincipalDocument
