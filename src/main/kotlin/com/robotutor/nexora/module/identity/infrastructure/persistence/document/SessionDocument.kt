package com.robotutor.nexora.module.identity.infrastructure.persistence.document

import com.robotutor.nexora.shared.persistence.document.MongoDocument
import com.robotutor.nexora.module.identity.domain.aggregate.Session
import com.robotutor.nexora.module.identity.domain.aggregate.SessionStatus
import com.robotutor.nexora.shared.outbox.persistence.document.PrincipalDataDocument
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
    val accountDataDocument: PrincipalDataDocument,
    val token: String,
    val issuedAt: Instant,
    val status: SessionStatus,
    @Indexed(name = "expireAtIndex", expireAfter = "0s")
    val expiresAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<Session>
