package com.robotutor.nexora.modules.auth.application.strategy

import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType

interface TokenGenerationStrategy {
    fun generate(
        principalType: TokenPrincipalType,
        principalContext: PrincipalContext,
        metadata: Map<String, String>
    ): Token
}