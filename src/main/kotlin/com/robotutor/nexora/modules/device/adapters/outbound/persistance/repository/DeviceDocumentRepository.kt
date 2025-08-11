package com.robotutor.nexora.modules.device.adapters.outbound.persistance.repository

import com.robotutor.nexora.modules.device.adapters.outbound.persistance.model.DeviceDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface DeviceDocumentRepository : ReactiveCrudRepository<DeviceDocument, String> {
    fun findByDeviceId(deviceId: String): Mono<DeviceDocument>
    fun findAllByPremisesIdAndDeviceIdIn(premisesId: String, deviceIds: List<String>): Flux<DeviceDocument>
    fun findByDeviceIdAndPremisesId(deviceId: String, premisesId: String): Mono<DeviceDocument>
}