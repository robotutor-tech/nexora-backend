package com.robotutor.nexora.module.zone.infrastructure.persistence

import com.robotutor.nexora.common.cache.annotation.Cache
import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.module.zone.domain.event.ZoneEventPublisher
import com.robotutor.nexora.module.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.module.zone.infrastructure.persistence.document.ZoneDocument
import com.robotutor.nexora.module.zone.infrastructure.persistence.mapper.ZoneDocumentMapper
import com.robotutor.nexora.module.zone.infrastructure.persistence.mapper.ZoneSpecificationTranslator
import com.robotutor.nexora.module.zone.infrastructure.persistence.repository.ZoneDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.common.persistence.repository.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoZoneRepository(
    private val zoneDocumentRepository: ZoneDocumentRepository,
    private val eventPublisher: ZoneEventPublisher,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) : ZoneRepository {

    override fun save(zone: ZoneAggregate): Mono<ZoneAggregate> {
        val zoneDocument = ZoneDocumentMapper.toMongoDocument(zone)
        return zoneDocumentRepository.save(zoneDocument)
            .retryOptimisticLockingFailure()
            .map { ZoneDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher, zone)
    }


    override fun findByPremisesIdAndName(premisesId: PremisesId, name: Name): Mono<ZoneAggregate> {
        return zoneDocumentRepository.findByPremisesIdAndName(premisesId.value, name.value)
            .map { ZoneDocumentMapper.toDomainModel(it) }
    }

    override fun findByZoneIdAndPremisesId(zoneId: ZoneId, premisesId: PremisesId): Mono<ZoneAggregate> {
        return zoneDocumentRepository.findByPremisesIdAndZoneId(premisesId.value, zoneId.value)
            .map { ZoneDocumentMapper.toDomainModel(it) }
    }

    @Cache("zone:zone-aggregate:zone-id:#{specification}")
    override fun findAll(specification: Specification<ZoneAggregate>): Flux<ZoneAggregate> {
        val query = Query(ZoneSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.find<ZoneDocument>(query)
            .map { ZoneDocumentMapper.toDomainModel(it) }
    }

    override fun existsByPremisesIdAndName(premisesId: PremisesId, name: Name): Mono<Boolean> {
        return zoneDocumentRepository.existsByPremisesIdAndName(premisesId.value, name.value)
    }


    override fun findAllByPremisesIdAndZoneIdIn(premisesId: PremisesId, zoneIds: List<ZoneId>): Flux<ZoneAggregate> {
        return Flux.empty()
    }


}