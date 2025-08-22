package com.robotutor.nexora.modules.iam.domain.repository

import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserContext
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface ActorRepository {
    fun save(actor: Actor): Mono<Actor>
    fun findByActorIdAndRoleId(actorId: ActorId, roleId: RoleId) :Mono<Actor>
    fun findAllByPrincipalTypeAndPrincipal(principalType: ActorPrincipalType, userContext: UserContext): Flux<Actor>
}