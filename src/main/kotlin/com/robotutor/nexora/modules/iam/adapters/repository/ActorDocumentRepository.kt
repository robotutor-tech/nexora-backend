package com.robotutor.nexora.modules.iam.adapters.repository

import com.robotutor.nexora.modules.iam.adapters.model.ActorDocument
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ActorDocumentRepository : ReactiveCrudRepository<ActorDocument, String> {
    fun findByActorIdAndRoleIdsContaining(actorId: String, roleId: String): Mono<ActorDocument>
    fun findAllByPrincipalTypeAndPrincipal(
        principalType: ActorPrincipalType,
        principal: PrincipalContext
    ): Flux<ActorDocument>
}