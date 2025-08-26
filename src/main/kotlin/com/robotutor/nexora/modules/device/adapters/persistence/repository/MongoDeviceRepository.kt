package com.robotutor.nexora.modules.device.adapters.persistence.repository

import com.robotutor.nexora.modules.device.adapters.persistence.model.DeviceDocument
import com.robotutor.nexora.modules.device.domain.model.Device
import com.robotutor.nexora.modules.device.domain.repository.DeviceRepository
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoDeviceRepository(private val deviceDocumentRepository: DeviceDocumentRepository) : DeviceRepository {
    override fun findByDeviceId(deviceId: DeviceId): Mono<Device> {
        return deviceDocumentRepository.findByDeviceId(deviceId.value)
            .map { it.toDomainModel() }
    }

    override fun save(device: Device): Mono<Device> {
        return deviceDocumentRepository.save(DeviceDocument.from(device))
            .map { it.toDomainModel() }
    }

    override fun findAllByPremisesIdAndDeviceIdsIn(premisesId: PremisesId, deviceIds: List<DeviceId>): Flux<Device> {
        return deviceDocumentRepository.findAllByPremisesIdAndDeviceIdIn(premisesId.value, deviceIds.map { it.value })
            .map { it.toDomainModel() }
    }

    override fun findByPremisesIdAndDeviceId(premisesId: PremisesId, deviceId: DeviceId): Mono<Device> {
        return deviceDocumentRepository.findByDeviceIdAndPremisesId(deviceId.value, premisesId.value)
            .map { it.toDomainModel() }
    }
}