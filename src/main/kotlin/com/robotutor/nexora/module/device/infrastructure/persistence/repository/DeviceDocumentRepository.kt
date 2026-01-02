package com.robotutor.nexora.module.device.infrastructure.persistence.repository

import com.robotutor.nexora.module.device.infrastructure.persistence.document.DeviceDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface DeviceDocumentRepository : ReactiveCrudRepository<DeviceDocument, String> {
    fun deleteByDeviceId(deviceId: String): Mono<DeviceDocument>
    fun findByDeviceId(deviceId: String): Mono<DeviceDocument>
}