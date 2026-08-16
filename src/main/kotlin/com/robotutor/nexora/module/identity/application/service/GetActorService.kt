package com.robotutor.nexora.module.identity.application.service

import com.robotutor.nexora.module.identity.application.command.GetActorQuery
import com.robotutor.nexora.module.identity.application.command.GetActorsQuery
import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.module.identity.domain.repository.ActorRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GetActorService(
    private val actorRepository: ActorRepository,
) {
    fun execute(query: GetActorsQuery): Flux<Actor> {
        return actorRepository.findAllByAccountId(query.accountId)
    }

    fun execute(query: GetActorQuery): Mono<Actor> {
        return actorRepository.findByActorIdAndPremisesId(query.actorId, query.premisesId)
    }
}
