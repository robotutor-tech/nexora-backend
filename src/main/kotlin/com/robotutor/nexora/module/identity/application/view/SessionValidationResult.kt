package com.robotutor.nexora.module.identity.application.view

import com.robotutor.nexora.shared.domain.vo.PrincipalData
import java.time.Instant

data class SessionValidationResult(
    val isValid: Boolean,
    val principalData: PrincipalData,
    val expiresAt: Instant,
)

