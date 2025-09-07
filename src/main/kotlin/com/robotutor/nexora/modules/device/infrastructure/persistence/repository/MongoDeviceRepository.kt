package com.robotutor.nexora.modules.device.infrastructure.persistence.repository

import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.modules.device.infrastructure.persistence.mapper.DeviceDocumentMapper
import com.robotutor.nexora.modules.device.infrastructure.persistence.document.DeviceDocument
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoDeviceRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Device, DeviceDocument>(mongoTemplate, DeviceDocument::class.java, DeviceDocumentMapper),
    DeviceRepository {
    override fun save(device: Device): Mono<Device> {
        val query = Query(Criteria.where("deviceId").`is`(device.deviceId.value))
        return this.findAndReplace(query, device)
    }

    override fun findByDeviceId(deviceId: DeviceId): Mono<Device> {
        val query = Query(Criteria.where("deviceId").`is`(deviceId.value))
        return this.findOne(query)
    }

    override fun findAllByPremisesIdAndDeviceIdsIn(premisesId: PremisesId, deviceIds: List<DeviceId>): Flux<Device> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("deviceId").`in`(deviceIds.map { it.value })
        )
        return this.findAll(query)
    }

    override fun findByPremisesIdAndDeviceId(premisesId: PremisesId, deviceId: DeviceId): Mono<Device> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("deviceId").`is`(deviceId.value)
        )
        return this.findOne(query)
    }
}