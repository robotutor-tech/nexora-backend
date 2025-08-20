package com.robotutor.nexora.modules.auth.domain.strategy

import com.robotutor.nexora.shared.domain.model.Identifier
import com.robotutor.nexora.modules.auth.domain.model.Token
import com.robotutor.nexora.shared.domain.model.TokenIdentifier

interface TokenGenerationStrategy {
    fun generate(identifier: Identifier<TokenIdentifier>, metadata: Map<String, Any?>?): Token
}