package com.robotutor.nexora.context.premises.application

import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service
class PremisesUseCase(
) {
    val logger = Logger(this::class.java)

//    fun getAllPremises(userData: UserData): Flux<ActorWithRolesPremises> {
//        return actorResourceFacade.getActors(userData).collectList()
//            .flatMapMany { actor ->
//                val premisesIds = actor.map { it.premisesId }.distinct()
//                premisesRepository.findAllByPremisesIdIn(premisesIds)
//                    .map { premises ->
//                        ActorWithRolesPremises(
//                            premisesAggregate = premises,
//                            actor = actor.find { it.premisesId == premises.premisesId }!!
//                        )
//                    }
//            }
//    }
//
//    fun getPremisesDetails(premisesId: PremisesId): Mono<PremisesAggregate> {
//        return premisesRepository.findByPremisesId(premisesId)
//    }
}