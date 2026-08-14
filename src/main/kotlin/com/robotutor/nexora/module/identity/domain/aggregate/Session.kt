package com.robotutor.nexora.module.identity.domain.aggregate

import com.robotutor.nexora.module.identity.domain.event.IAMEvent
import com.robotutor.nexora.module.identity.domain.vo.HashAccessToken
import com.robotutor.nexora.module.identity.domain.vo.SessionId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import java.time.Instant

class Session private constructor(
    val sessionId: SessionId,
    val accountData: AccountData,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val token: HashAccessToken,
    private var status: SessionStatus,
) : AggregateRoot<Session, SessionId, IAMEvent>(sessionId) {

    fun getStatus(): SessionStatus {
        return status
    }


    companion object {
        fun create(
            sessionId: SessionId,
            accountData: AccountData,
            issuedAt: Instant,
            expiredAt: Instant,
            token: HashAccessToken,
            status: SessionStatus,
        ): Session {
            return Session(
                sessionId = sessionId,
                accountData = accountData,
                token = token,
                issuedAt = issuedAt,
                expiresAt = expiredAt,
                status = status
            )
        }

        fun register(
            sessionId: SessionId,
            accountData: AccountData,
            token: HashAccessToken,
            expiresAt: Instant
        ): Session {
            return create(
                sessionId = sessionId,
                accountData = accountData,
                token = token,
                expiredAt = expiresAt,
                issuedAt = Instant.now(),
                status = SessionStatus.ACTIVE
            )
        }
    }
}

enum class SessionStatus {
    ACTIVE, REVOKED, EXPIRED
}
