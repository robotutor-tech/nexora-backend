package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.domain.vo.ActorData
import com.robotutor.nexora.common.security.domain.vo.ActorPrincipalContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActorDataRetrieverStrategy(
//    private val actorDataRetriever: ActorDataRetriever,
) : DataRetrieverStrategy<ActorPrincipalContext, ActorData> {
    override fun getPrincipalData(context: ActorPrincipalContext): Mono<ActorData> {
        return Mono.empty()
//        return actorDataRetriever.getActorData(context.actorId, context.roleId)
//            .flatMap { response ->
//                actorDataRetrieverStrategyFactory.getStrategy(response.principalType)
//                    .getPrincipalData(response.principal)
//                    .map {
//                        ActorData(
//                            actorId = response.actorId,
//                            role = response.role,
//                            premisesId = response.premisesId,
//                            principalType = response.principalType,
//                            principal = it
//                        )
//                    }
//            }
    }
}