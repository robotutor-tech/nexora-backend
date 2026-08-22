package com.robotutor.nexora.module.identity.infrastructure.persistence

import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.repository.ActorRepository
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.ActorDocument
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.ActorDocumentMapper
import com.robotutor.nexora.module.identity.infrastructure.persistence.mapper.ActorSpecificationTranslator
import com.robotutor.nexora.module.identity.infrastructure.persistence.repository.ActorDocumentRepository
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.message.mapper.EventMapper
import com.robotutor.nexora.shared.outbox.publishEvents
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.exists
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoActorRepository(
    private val actorDocumentRepository: ActorDocumentRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val eventMapper: EventMapper<IdentityEvent>,
) : ActorRepository {
    override fun save(actor: Actor): Mono<Actor> {
        val actorDocument = ActorDocumentMapper.toDocument(actor)
        return actorDocumentRepository.save(actorDocument)
            .retryOptimisticLockingFailure()
            .map { ActorDocumentMapper.toDomain(it) }
            .publishEvents(actor, eventMapper)
    }

    override fun findAllByAccountId(accountId: AccountId): Flux<Actor> {
        return actorDocumentRepository.findAllByAccountId(accountId.value)
            .map { ActorDocumentMapper.toDomain(it) }
    }

    override fun findByAccountIdAndPremisesId(accountId: AccountId, premisesId: PremisesId): Mono<Actor> {
        return actorDocumentRepository.findByAccountIdAndPremisesId(accountId.value, premisesId.value)
            .map { ActorDocumentMapper.toDomain(it) }
    }

    override fun findByActorIdAndPremisesId(actorId: ActorId, premisesId: PremisesId): Mono<Actor> {
        return actorDocumentRepository.findByActorIdAndPremisesId(actorId.value, premisesId.value)
            .map { ActorDocumentMapper.toDomain(it) }
    }

    override fun findByActorId(actorId: ActorId): Mono<Actor> {
        return actorDocumentRepository.findByActorId(actorId.value)
            .map { ActorDocumentMapper.toDomain(it) }
    }

    override fun findBySpecification(specification: Specification<Actor>): Mono<Actor> {
        val query = Query(ActorSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.findOne<ActorDocument>(query)
            .map { ActorDocumentMapper.toDomain(it) }
    }

    override fun exitsBySpecification(specification: Specification<Actor>): Mono<Boolean> {
        val query = Query(ActorSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.exists<ActorDocument>(query)
    }
}
