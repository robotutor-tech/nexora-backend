package com.robotutor.nexora.context.iam.application.view

import com.robotutor.nexora.context.iam.domain.vo.SessionPrincipal
import java.time.Instant

data class SessionValidationResult(
    val isValid: Boolean,
    val sessionPrincipal: SessionPrincipal,
    val expiresAt: Instant,
)

