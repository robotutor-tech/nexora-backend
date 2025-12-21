package com.robotutor.nexora.context.device.infrastructure.persistence

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.event.DeviceDomainEvent
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.infrastructure.persistence.document.DeviceDocument
import com.robotutor.nexora.context.device.infrastructure.persistence.mapper.DeviceDocumentMapper
import com.robotutor.nexora.context.device.infrastructure.persistence.mapper.DeviceSpecificationTranslator
import com.robotutor.nexora.context.device.infrastructure.persistence.repository.DeviceDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoDeviceRepository(
    private val deviceDocumentRepository: DeviceDocumentRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    private val eventPublisher: DomainEventPublisher<DeviceDomainEvent>,
) : DeviceRepository {
    override fun save(device: DeviceAggregate): Mono<DeviceAggregate> {
        val document = DeviceDocumentMapper.toMongoDocument(device)
        return deviceDocumentRepository.save(document)
            .retryOptimisticLockingFailure()
            .map { DeviceDocumentMapper.toDomainModel(it) }
            .publishEvents(eventPublisher)
    }

    override fun findByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate> {
        return deviceDocumentRepository.findByDeviceId(deviceId.value)
            .map { DeviceDocumentMapper.toDomainModel(it) }
    }

    override fun deleteByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate> {
        return deviceDocumentRepository.deleteByDeviceId(deviceId.value)
            .map { DeviceDocumentMapper.toDomainModel(it) }
    }

    override fun findAll(specification: Specification<DeviceAggregate>): Flux<DeviceAggregate> {
        val query = Query(DeviceSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.find<DeviceDocument>(query)
            .map { DeviceDocumentMapper.toDomainModel(it) }
    }

    override fun findByAccountId(accountId: AccountId): Mono<DeviceAggregate> {
        return deviceDocumentRepository.findByAccountId(accountId.value)
            .map{ DeviceDocumentMapper.toDomainModel(it) }
    }
}