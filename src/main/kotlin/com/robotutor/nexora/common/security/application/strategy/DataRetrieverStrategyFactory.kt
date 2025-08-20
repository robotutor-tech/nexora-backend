package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.shared.domain.model.TokenIdentifier
import org.springframework.stereotype.Service

@Service
class DataRetrieverStrategyFactory(
    private val userDataRetrieverStrategy: UserDataRetrieverStrategy,
    private val internalUserDataRetrieverStrategy: InternalUserDataRetrieverStrategy,
) {
    fun getStrategy(tokenIdentifier: TokenIdentifier): DataRetrieverStrategy {
        return when (tokenIdentifier) {
            TokenIdentifier.USER -> userDataRetrieverStrategy
            TokenIdentifier.INTERNAL -> internalUserDataRetrieverStrategy
            else -> throw IllegalArgumentException("Invalid token identifier")
        }
    }
}