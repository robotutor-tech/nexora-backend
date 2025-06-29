package com.robotutor.nexora.device.repositories

import com.robotutor.nexora.device.models.Device
import com.robotutor.nexora.device.models.DeviceId
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface DeviceRepository : ReactiveCrudRepository<Device, DeviceId> {
    fun findByDeviceId(deviceId: DeviceId): Mono<Device>
    fun findByDeviceIdAndPremisesId(deviceId: DeviceId, premisesId: PremisesId): Mono<Device>
    fun findAllByPremisesIdAndDeviceIdIn(premisesId: PremisesId, deviceIds: List<DeviceId>): Flux<Device>
}
