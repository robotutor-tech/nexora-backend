package com.robotutor.nexora.shared.security.domain.vo

import com.robotutor.nexora.shared.domain.vo.principal.AccountData

data class SessionValidationResult(
    val isValid: Boolean,
    val accountData: AccountData,
    val expiresIn: Number,
)

