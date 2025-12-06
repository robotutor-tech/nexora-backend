package com.robotutor.nexora.context.iam.domain.strategy

import com.robotutor.nexora.context.iam.domain.aggregate.TokenAggregate
import com.robotutor.nexora.context.iam.domain.aggregate.TokenPrincipalType
import com.robotutor.nexora.context.iam.domain.vo.TokenPrincipalContext

interface TokenGenerationStrategy {
    fun generate(
        principalType: TokenPrincipalType,
        principalContext: TokenPrincipalContext,
    ): TokenAggregate
}