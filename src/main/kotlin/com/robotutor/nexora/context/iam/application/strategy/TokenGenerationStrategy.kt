package com.robotutor.nexora.context.iam.application.strategy

import com.robotutor.nexora.context.iam.domain.entity.Token
import com.robotutor.nexora.context.iam.domain.entity.TokenPrincipalType
import com.robotutor.nexora.shared.domain.model.PrincipalContext

interface TokenGenerationStrategy {
    fun generate(
        principalType: TokenPrincipalType,
        principalContext: PrincipalContext,
    ): Token
}