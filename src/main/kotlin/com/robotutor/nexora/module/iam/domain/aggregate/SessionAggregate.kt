package com.robotutor.nexora.module.iam.domain.aggregate

import com.robotutor.nexora.module.iam.domain.event.IAMEvent
import com.robotutor.nexora.module.iam.domain.exception.IAMError
import com.robotutor.nexora.module.iam.domain.vo.AccessToken
import com.robotutor.nexora.module.iam.domain.vo.HashedTokenValue
import com.robotutor.nexora.module.iam.domain.vo.SessionId
import com.robotutor.nexora.module.iam.domain.vo.SessionPrincipal
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
    private var accessToken: AccessToken,
    private var refreshTokenHashValue: HashedTokenValue,
    private var refreshCountValue: Int = 0,
    private var lastRefreshAtValue: Instant = Instant.now(),
) : AggregateRoot<SessionAggregate, SessionId, IAMEvent>(sessionId) {

    val refreshTokenHash: HashedTokenValue get() = refreshTokenHashValue
    fun getAccessToken(): AccessToken = accessToken
    val refreshCount: Int get() = refreshCountValue
    val lastRefreshAt: Instant get() = lastRefreshAtValue
    val status: SessionStatus get() = statusValue


    companion object {
        private const val MAX_REFRESH_COUNT = 50
        fun create(
            sessionPrincipal: SessionPrincipal,
            accessToken: AccessToken,
            refreshTokenHash: HashedTokenValue,
        ): SessionAggregate {
            return SessionAggregate(
                sessionId = SessionId.generate(),
                sessionPrincipal = sessionPrincipal,
                accessToken = accessToken,
                refreshTokenHashValue = refreshTokenHash,
            )
        }
    }

    fun refresh(accessToken: AccessToken, refreshTokenHash: HashedTokenValue): SessionAggregate {
        if (!canRefresh()) {
            throw UnAuthorizedException(IAMError.NEXORA0206)
        } else {
            refreshCountValue += 1
            lastRefreshAtValue = Instant.now()
            refreshTokenHashValue = refreshTokenHash
            this.accessToken = accessToken
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