package com.robotutor.nexora.module.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.time.Instant

data class TokenPayload(
    val sessionPrincipal: SessionPrincipal,
    val expiresAt: Instant = Instant.now().plusSeconds(3600)
) : ValueObject

