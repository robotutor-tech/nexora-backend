package com.robotutor.nexora.module.device.infrastructure.persistence

import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.domain.repository.DeviceRepository
import com.robotutor.nexora.module.device.infrastructure.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.module.device.infrastructure.persistence.document.DeviceDocument
import com.robotutor.nexora.module.device.infrastructure.persistence.mapper.DeviceDocumentMapper
import com.robotutor.nexora.module.device.infrastructure.persistence.mapper.DeviceSpecificationTranslator
import com.robotutor.nexora.module.device.infrastructure.persistence.repository.DeviceDocumentRepository
import com.robotutor.nexora.shared.domain.specification.Specification
import com.robotutor.nexora.shared.domain.vo.DeviceId
import com.robotutor.nexora.shared.outbox.publishEvents
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoDeviceRepository(
    private val deviceDocumentRepository: DeviceDocumentRepository,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
) : DeviceRepository {
    override fun save(device: DeviceAggregate): Mono<DeviceAggregate> {
        val document = DeviceDocumentMapper.toDocument(device)
        return deviceDocumentRepository.save(document)
            .retryOptimisticLockingFailure()
            .map { DeviceDocumentMapper.toDomain(it) }
            .publishEvents(device, DeviceEventMapper)
    }

    override fun findByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate> {
        return deviceDocumentRepository.findByDeviceId(deviceId.value)
            .map { DeviceDocumentMapper.toDomain(it) }
    }

    override fun deleteByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate> {
        return deviceDocumentRepository.deleteByDeviceId(deviceId.value)
            .map { DeviceDocumentMapper.toDomain(it) }
    }

    override fun findAll(specification: Specification<DeviceAggregate>): Flux<DeviceAggregate> {
        val query = Query(DeviceSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.find<DeviceDocument>(query)
            .map { DeviceDocumentMapper.toDomain(it) }
    }

    override fun findBySpecification(specification: Specification<DeviceAggregate>): Mono<DeviceAggregate> {
        val query = Query(DeviceSpecificationTranslator.translate(specification))
        return reactiveMongoTemplate.findOne<DeviceDocument>(query)
            .map { DeviceDocumentMapper.toDomain(it) }
    }
}
