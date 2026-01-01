package com.robotutor.nexora.context.iam.infrastructure.persistence.document

import com.robotutor.nexora.context.iam.domain.aggregate.SessionAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.SessionStatus
import com.robotutor.nexora.common.persistence.document.MongoDocument
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
    val principal: SessionPrincipalDocument,
    val accessToken: String,
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
