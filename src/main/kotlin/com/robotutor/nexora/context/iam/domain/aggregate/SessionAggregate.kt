package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.exception.NexoraError
import com.robotutor.nexora.context.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.context.iam.domain.vo.SessionId
import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import java.time.Instant
import java.time.temporal.ChronoUnit

data class SessionAggregate(
    val sessionId: SessionId,
    val sessionPrincipal: SessionPrincipal,
    val issuedAt: Instant = Instant.now(),
    val expiresAt: Instant = Instant.now().plus(7, ChronoUnit.DAYS),
    private var statusValue: SessionStatus = SessionStatus.ACTIVE,
    private var refreshTokenHashValue: HashedTokenValue,
    private var refreshCountValue: Int = 0,
    private var lastRefreshAtValue: Instant = Instant.now(),
) : AggregateRoot<SessionAggregate, SessionId, IAMDomainEvent>(sessionId) {

    val refreshTokenHash: HashedTokenValue get() = refreshTokenHashValue
    val refreshCount: Int get() = refreshCountValue
    val lastRefreshAt: Instant get() = lastRefreshAtValue
    val status: SessionStatus get() = statusValue


    companion object {
        private const val MAX_REFRESH_COUNT = 50
        fun create(
            sessionPrincipal: SessionPrincipal,
            refreshTokenHash: HashedTokenValue,
        ): SessionAggregate {
            return SessionAggregate(
                sessionId = SessionId.generate(),
                sessionPrincipal = sessionPrincipal,
                refreshTokenHashValue = refreshTokenHash,
            )
        }
    }

    fun refresh(refreshTokenHash: HashedTokenValue): SessionAggregate {
        if (!canRefresh()) {
            throw UnAuthorizedException(NexoraError.NEXORA0206)
        } else {
            refreshCountValue += 1
            lastRefreshAtValue = Instant.now()
            refreshTokenHashValue = refreshTokenHash
            return this
        }
    }

    fun revoke(): SessionAggregate {
        this.statusValue = SessionStatus.REVOKED
        return this
    }

    private fun canRefresh(): Boolean {
        return statusValue == SessionStatus.ACTIVE && refreshCountValue < MAX_REFRESH_COUNT && this.expiresAt.isAfter(
            Instant.now()
        )
    }
}

enum class SessionStatus {
    ACTIVE, REVOKED, EXPIRED
}