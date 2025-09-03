package com.robotutor.nexora.modules.auth.application.strategy

import com.robotutor.nexora.modules.auth.domain.entity.Token
import com.robotutor.nexora.modules.auth.domain.entity.TokenPrincipalType
import com.robotutor.nexora.shared.domain.model.PrincipalContext

interface TokenGenerationStrategy {
    fun generate(
        principalType: TokenPrincipalType,
        principalContext: PrincipalContext,
    ): Token
}