package com.robotutor.nexora.iam.repositories

import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ActorRepository : ReactiveCrudRepository<Actor, ActorId> {
    fun findAllByIdentifier_TypeAndIdentifier_Id(identifier: ActorIdentifier, id: String): Flux<Actor>
    fun findByActorId(actorId: ActorId): Mono<Actor>
}
