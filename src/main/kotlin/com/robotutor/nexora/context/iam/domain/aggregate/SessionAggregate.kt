package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.SessionId
import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal
import com.robotutor.nexora.shared.domain.AggregateRoot
import java.time.Instant
import java.time.temporal.ChronoUnit

data class SessionAggregate(
    val sessionId: SessionId,
    val sessionPrincipal: SessionPrincipal,
    val refreshTokenHash: HashedTokenValue,
    val refreshCount: Int = 0,
    val status: SessionStatus = SessionStatus.ACTIVE,
    val issuedAt: Instant = Instant.now(),
    val lastRefreshAt: Instant = Instant.now(),
    val expiresAt: Instant = Instant.now().plus(7, ChronoUnit.DAYS),
    val version: Long? = null
) : AggregateRoot<SessionAggregate, SessionId, IAMEvent>(sessionId) {
    companion object {
        fun create(
            sessionPrincipal: SessionPrincipal,
            refreshTokenHash: HashedTokenValue,
        ): SessionAggregate {
            return SessionAggregate(
                sessionId = SessionId.generate(),
                sessionPrincipal = sessionPrincipal,
                refreshTokenHash = refreshTokenHash,
            )
        }
    }

    fun refresh(): SessionAggregate {
        return this.copy()
    }
}

enum class SessionStatus {
    ACTIVE, REVOKED, EXPIRED
}