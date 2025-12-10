package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.shared.domain.vo.ValueObject
import java.time.Instant

data class TokenPayload(
    val sessionPrincipal: SessionPrincipal,
    val expiresAt: Instant,
) : ValueObject()

