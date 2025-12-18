package com.robotutor.nexora.context.device.infrastructure.persistence

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.event.DeviceDomainEvent
import com.robotutor.nexora.context.device.domain.repository.DeviceRepository
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.infrastructure.persistence.mapper.DeviceDocumentMapper
import com.robotutor.nexora.context.device.infrastructure.persistence.repository.DeviceDocumentRepository
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoDeviceRepository(
    private val deviceDocumentRepository: DeviceDocumentRepository,
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

//    override fun findByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate> {
//        return
////        val query = Query(Criteria.where("deviceId").`is`(deviceId.value))
////        return this.findOne(query)
//    }
//
//    override fun findAllByPremisesIdAndDeviceIdsIn(
//        premisesId: PremisesId,
//        deviceIds: List<DeviceId>
//    ): Flux<DeviceAggregate> {
////        val query = Query(
////            Criteria.where("premisesId").`is`(premisesId.value)
////                .and("deviceId").`in`(deviceIds.map { it.value })
////        )
////        return this.findAll(query)
//    }
//
//    override fun findByPremisesIdAndDeviceId(premisesId: PremisesId, deviceId: DeviceId): Mono<DeviceAggregate> {
////        val query = Query(
////            Criteria.where("premisesId").`is`(premisesId.value)
////                .and("deviceId").`is`(deviceId.value)
////        )
////        return this.findOne(query)
//    }

}