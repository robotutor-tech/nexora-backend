package com.robotutor.nexora.module.premises.infrastructure.persistence

import com.robotutor.nexora.shared.cache.annotation.Cache
import com.robotutor.nexora.shared.cache.annotation.CacheEvicts
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import com.robotutor.nexora.module.premises.domain.aggregate.Premises
import com.robotutor.nexora.module.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.module.premises.infrastructure.persistence.mapper.PremisesDocumentMapper
import com.robotutor.nexora.module.premises.infrastructure.persistence.repository.PremisesDocumentRepository
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoPremisesRepository(
    val premisesDocumentRepository: PremisesDocumentRepository,
//    val eventPublisher: PremisesEventPublisher,
) : PremisesRepository {
    @CacheEvicts(["premises:premises-aggregate:premises-id:#{premisesAggregate.premisesId.value}"])
    override fun save(premises: Premises): Mono<Premises> {
        val premisesDocument = PremisesDocumentMapper.toMongoDocument(premises)
        return premisesDocumentRepository.save(premisesDocument)
            .retryOptimisticLockingFailure()
            .map { PremisesDocumentMapper.toDomainModel(it) }
//            .publishEvents(eventPublisher, premisesAggregate)
    }

    override fun findAllByPremisesIdIn(premisesIds: List<PremisesId>): Flux<Premises> {
        return premisesDocumentRepository.findAllByPremisesIdIn(premisesIds.map { it.value })
            .map { PremisesDocumentMapper.toDomainModel(it) }
    }

    @Cache("premises:premises-aggregate:premises-id:#{premisesId.value}")
    override fun findByPremisesId(premisesId: PremisesId): Mono<Premises> {
        return premisesDocumentRepository.findByPremisesId(premisesId.value)
            .map { PremisesDocumentMapper.toDomainModel(it) }
    }

    @CacheEvicts(["premises:premises-aggregate:premises-id:#{premisesId.value}"])
    override fun deleteByPremisesIdAndOwnerId(premisesId: PremisesId, ownerId: AccountId): Mono<Premises> {
        return premisesDocumentRepository.deleteByPremisesIdAndOwnerId(premisesId.value, ownerId.value)
            .map { PremisesDocumentMapper.toDomainModel(it) }
    }
}
