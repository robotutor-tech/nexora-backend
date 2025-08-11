package com.robotutor.nexora.modules.iam.repositories

import com.robotutor.nexora.modules.iam.models.Actor
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.ActorIdentifier
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ActorRepository : ReactiveCrudRepository<Actor, ActorId> {
    fun findAllByIdentifier_TypeAndIdentifier_Id(identifier: ActorIdentifier, id: String): Flux<Actor>
    fun findByActorIdAndRolesContaining(actorId: ActorId, roleId: List<RoleId>): Mono<Actor>
}
