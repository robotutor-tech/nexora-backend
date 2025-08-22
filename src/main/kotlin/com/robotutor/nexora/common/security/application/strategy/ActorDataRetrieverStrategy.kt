package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.application.ports.ActorDataRetriever
import com.robotutor.nexora.shared.domain.model.ActorContext
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.PrincipalData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActorDataRetrieverStrategy(
    private val actorDataRetriever: ActorDataRetriever,
    private val actorDataRetrieverStrategyFactory: ActorDataRetrieverStrategyFactory,
) : DataRetrieverStrategy {
    override fun getPrincipalData(principalContext: PrincipalContext): Mono<PrincipalData> {
        val actorContext = principalContext as ActorContext
        return actorDataRetriever.getActorData(actorContext.actorId, actorContext.roleId)
            .flatMap { response ->
                actorDataRetrieverStrategyFactory.getStrategy(response.principalType)
                    .getPrincipalData(response.principal)
                    .map {
                        ActorData(
                            actorId = response.actorId,
                            role = response.role,
                            premisesId = response.premisesId,
                            principalType = response.principalType,
                            principal = it
                        )
                    }
            }
    }
}