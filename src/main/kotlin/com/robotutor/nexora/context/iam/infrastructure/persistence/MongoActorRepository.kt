package com.robotutor.nexora.context.iam.infrastructure.persistence

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.repository.ActorRepository
import com.robotutor.nexora.context.iam.infrastructure.persistence.mapper.ActorDocumentMapper
import com.robotutor.nexora.context.iam.infrastructure.persistence.repository.ActorDocumentRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoActorRepository(
    private val actorDocumentRepository: ActorDocumentRepository,
    private val eventPublisher: EventPublisher<IAMDomainEvent>,
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
}