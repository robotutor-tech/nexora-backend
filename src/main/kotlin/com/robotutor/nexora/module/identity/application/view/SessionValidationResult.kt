package com.robotutor.nexora.module.identity.application.view

import com.robotutor.nexora.shared.domain.vo.AccountData
import java.time.Instant

data class SessionValidationResult(
    val isValid: Boolean,
    val accountData: AccountData,
    val expiresAt: Instant,
)

