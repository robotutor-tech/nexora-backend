package com.robotutor.nexora.modules.device.domain.repository

import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceRepository {
    fun findByDeviceId(deviceId: DeviceId): Mono<Device>
    fun save(device: Device): Mono<Device>
    fun findAllByPremisesIdAndDeviceIdsIn(premisesId: PremisesId, deviceIds: List<DeviceId>): Flux<Device>
    fun findByPremisesIdAndDeviceId(premisesId: PremisesId, deviceId: DeviceId): Mono<Device>
}