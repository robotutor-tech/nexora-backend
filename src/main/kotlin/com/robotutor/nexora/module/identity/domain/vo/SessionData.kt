package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.vo.ValueObject
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import java.time.Instant

data class SessionData(
    val sessionId: SessionId,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val accountData: AccountData,
    val expiresIn: Long = expiresAt.toEpochMilli() - Instant.now().toEpochMilli(),
) : ValueObject
