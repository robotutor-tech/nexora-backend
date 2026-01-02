package com.robotutor.nexora.module.iam.application.service

import com.robotutor.nexora.module.iam.application.command.GetActorQuery
import com.robotutor.nexora.module.iam.application.command.GetActorsQuery
import com.robotutor.nexora.module.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.module.iam.domain.repository.ActorRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class GetActorService(
    private val actorRepository: ActorRepository,
) {
    fun execute(query: GetActorsQuery): Flux<ActorAggregate> {
        return actorRepository.findAllByAccountId(query.accountId)
    }

    fun execute(query: GetActorQuery): Mono<ActorAggregate> {
        return actorRepository.findByActorIdAndPremisesId(query.actorId, query.premisesId)
    }
}