package com.robotutor.nexora.modules.iam.adapters.repository

import com.robotutor.nexora.modules.iam.adapters.model.ActorDocument
import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.modules.iam.domain.repository.ActorRepository
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoActorRepository(private val actorDocumentRepository: ActorDocumentRepository) : ActorRepository {
    override fun save(actor: Actor): Mono<Actor> {
        return actorDocumentRepository.save(ActorDocument.from(actor))
            .map { it.toDomainModel() }
    }

    override fun findByActorIdAndRoleId(actorId: ActorId, roleId: RoleId): Mono<Actor> {
        return actorDocumentRepository.findByActorIdAndRoleIdsContaining(actorId.value, roleId.value)
            .map { it.toDomainModel() }
    }

    override fun findAllByPrincipalTypeAndPrincipal(
        principalType: ActorPrincipalType,
        userContext: UserContext
    ): Flux<Actor> {
        return actorDocumentRepository.findAllByPrincipalTypeAndPrincipal(principalType, userContext)
            .map { it.toDomainModel() }
    }
}