package com.robotutor.nexora.common.security.application.strategy.factory

import com.robotutor.nexora.common.security.application.strategy.DataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.DeviceDataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.UserDataRetrieverStrategy
import com.robotutor.nexora.shared.domain.model.ActorPrincipalContext
import com.robotutor.nexora.shared.domain.model.ActorPrincipalData
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import org.springframework.stereotype.Service

@Service
class ActorDataRetrieverStrategyFactory(
    private val userDataRetrieverStrategy: UserDataRetrieverStrategy,
    private val deviceDataRetrieverStrategy: DeviceDataRetrieverStrategy,
) {
    fun getStrategy(actorPrincipalType: ActorPrincipalType): DataRetrieverStrategy<ActorPrincipalContext, ActorPrincipalData> {
        @Suppress("UNCHECKED_CAST")
        return when (actorPrincipalType) {
            ActorPrincipalType.USER -> userDataRetrieverStrategy
            ActorPrincipalType.DEVICE -> deviceDataRetrieverStrategy
        } as DataRetrieverStrategy<ActorPrincipalContext, ActorPrincipalData>
    }
}