package com.robotutor.nexora.context.iam.infrastructure.persistence.document

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.SessionStatus
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val SESSION_COLLECTION = "sessions"

@TypeAlias("Session")
@Document(SESSION_COLLECTION)
data class SessionDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val sessionId: String,
    val sessionPrincipal: SessionPrincipalDocument,
    val refreshTokenHash: String,
    val refreshCount: Int = 0,
    val status: SessionStatus = SessionStatus.ACTIVE,
    val issuedAt: Instant,
    val lastRefreshAt: Instant,
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    val expiresAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<SessionAggregate>

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountPrincipalDocument::class, name = "ACCOUNT"),
    JsonSubTypes.Type(value = ActorPrincipalDocument::class, name = "ACTOR")
)
sealed interface SessionPrincipalDocument
data class AccountPrincipalDocument(
    val accountId: String,
    val type: AccountType
) : SessionPrincipalDocument

data class ActorPrincipalDocument(
    val actorId: String,
    val premisesId: String,
    val accountId: String,
    val type: AccountType,
) : SessionPrincipalDocument
