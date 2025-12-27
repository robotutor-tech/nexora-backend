package com.robotutor.nexora.common.security.domain.vo

import com.robotutor.nexora.shared.domain.vo.principal.PrincipalData

data class SessionValidationResult(
    val isValid: Boolean,
    val principalData: PrincipalData,
    val expiresIn: Number,
)

