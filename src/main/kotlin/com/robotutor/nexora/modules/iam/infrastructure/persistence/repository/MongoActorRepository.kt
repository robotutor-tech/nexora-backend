package com.robotutor.nexora.modules.iam.adapters.persistence.repository

import com.robotutor.nexora.modules.iam.infrastructure.persistence.mapper.ActorDocumentMapper
import com.robotutor.nexora.modules.iam.infrastructure.persistence.document.ActorDocument
import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.modules.iam.domain.repository.ActorRepository
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.ActorPrincipalType
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.UserContext
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoActorRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Actor, ActorDocument>(mongoTemplate, ActorDocument::class.java, ActorDocumentMapper()),
    ActorRepository {
    override fun save(actor: Actor): Mono<Actor> {
        val query = Query(Criteria.where("actorId").`is`(actor.actorId.value))
        return this.findAndReplace(query, actor)
    }

    override fun findByActorIdAndRoleId(actorId: ActorId, roleId: RoleId): Mono<Actor> {
        val query = Query(
            Criteria.where("actorId").`is`(actorId.value)
                .and("roleIds").`in`(listOf(roleId.value))
        )
        return this.findOne(query)
    }

    override fun findAllByPrincipalTypeAndPrincipal(principalType: ActorPrincipalType, userContext: UserContext): Flux<Actor> {
        val query = Query(
            Criteria.where("principalType").`is`(principalType)
                .and("principal.userId").`is`(userContext.userId.value)
        )
        return this.findAll(query)
    }
}
