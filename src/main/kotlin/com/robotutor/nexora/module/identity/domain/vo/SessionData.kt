package com.robotutor.nexora.module.identity.domain.vo

import com.robotutor.nexora.shared.domain.vo.ValueObject
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import java.time.Instant

data class SessionData(
    val sessionId: SessionId,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val principalData: PrincipalData,
    val expiresIn: Long = expiresAt.toEpochMilli() - Instant.now().toEpochMilli(),
) : ValueObject
