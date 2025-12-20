package com.robotutor.nexora.context.premises.infrastructure.persistence

import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.event.PremisesDomainEvent
import com.robotutor.nexora.context.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.context.premises.infrastructure.persistence.mapper.PremisesDocumentMapper
import com.robotutor.nexora.context.premises.infrastructure.persistence.repository.PremisesDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoPremisesRepository(
    val premisesDocumentRepository: PremisesDocumentRepository,
    val eventPublisher: DomainEventPublisher<PremisesDomainEvent>,
) : PremisesRepository {
    override fun save(premisesAggregate: PremisesAggregate): Mono<PremisesAggregate> {
        val premisesDocument = PremisesDocumentMapper.toMongoDocument(premisesAggregate)
        return premisesDocumentRepository.save(premisesDocument)
            .retryOptimisticLockingFailure()
            .map { PremisesDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, premisesAggregate)
    }

    override fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<PremisesAggregate> {
        return premisesDocumentRepository.findAllByPremisesIdIn(premisesIds.map { it.value })
            .map { PremisesDocumentMapper.toDomainModel(it) }
    }

    override fun findByPremisesId(premisesId: PremisesId): Mono<PremisesAggregate> {
        return premisesDocumentRepository.findByPremisesId(premisesId.value)
            .map { PremisesDocumentMapper.toDomainModel(it) }
    }

    override fun deleteByPremisesIdAndOwnerId(premisesId: PremisesId, ownerId: AccountId): Mono<PremisesAggregate> {
        return premisesDocumentRepository.deleteByPremisesIdAndOwnerId(premisesId.value, ownerId.value)
            .map { PremisesDocumentMapper.toDomainModel(it) }
    }
}