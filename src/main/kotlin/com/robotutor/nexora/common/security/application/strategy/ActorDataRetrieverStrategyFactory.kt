package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import org.springframework.stereotype.Service

@Service
class ActorDataRetrieverStrategyFactory(
    private val userDataRetrieverStrategy: UserDataRetrieverStrategy,
) {
    fun getStrategy(actorPrincipalType: ActorPrincipalType): DataRetrieverStrategy {
        return when (actorPrincipalType) {
            ActorPrincipalType.USER -> userDataRetrieverStrategy
            else -> throw IllegalArgumentException("Invalid token identifier")
        }
    }
}