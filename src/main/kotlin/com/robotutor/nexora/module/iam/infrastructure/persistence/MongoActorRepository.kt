package com.robotutor.nexora.module.iam.infrastructure.persistence

import com.robotutor.nexora.module.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.module.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.iam.domain.repository.ActorRepository
import com.robotutor.nexora.module.iam.infrastructure.persistence.document.ActorDocument
import com.robotutor.nexora.module.iam.infrastructure.persistence.mapper.ActorDocumentMapper
import com.robotutor.nexora.module.iam.infrastructure.persistence.mapper.ActorSpecificationTranslator
import com.robotutor.nexora.module.iam.infrastructure.persistence.repository.ActorDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.common.persistence.repository.retryOptimisticLockingFailure
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
    private val eventPublisher: IAMEventPublisher,
) : ActorRepository {
    override fun save(actorAggregate: ActorAggregate): Mono<ActorAggregate> {
        val actorDocument = ActorDocumentMapper.toMongoDocument(actorAggregate)
        return actorDocumentRepository.save(actorDocument)
            .retryOptimisticLockingFailure()
            .map { ActorDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, actorAggregate)
    }

    override fun findAllByAccountId(accountId: AccountId): Flux<ActorAggregate> {
        return actorDocumentRepository.findAllByAccountId(accountId.value)
            .map { ActorDocumentMapper.toDomainModel(it) }
    }

    override fun findByAccountIdAndPremisesId(accountId: AccountId, premisesId: PremisesId): Mono<ActorAggregate> {
        return actorDocumentRepository.findByAccountIdAndPremisesId(accountId.value, premisesId.value)
            .map { ActorDocumentMapper.toDomainModel(it) }
    }

    override fun findByActorIdAndPremisesId(actorId: ActorId, premisesId: PremisesId): Mono<ActorAggregate> {
        return actorDocumentRepository.findByActorIdAndPremisesId(actorId.value, premisesId.value)
            .map { ActorDocumentMapper.toDomainModel(it) }
    }

    override fun findByActorId(actorId: ActorId): Mono<ActorAggregate> {
        return actorDocumentRepository.findByActorId(actorId.value)
            .map { ActorDocumentMapper.toDomainModel(it) }
    }

    override fun findBySpecification(specification: Specification<ActorAggregate>): Mono<ActorAggregate> {
        val query = Query(ActorSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.findOne<ActorDocument>(query)
            .map { ActorDocumentMapper.toDomainModel(it) }
    }

    override fun exitsBySpecification(specification: Specification<ActorAggregate>): Mono<Boolean> {
        val query = Query(ActorSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.exists<ActorDocument>(query)
    }
}