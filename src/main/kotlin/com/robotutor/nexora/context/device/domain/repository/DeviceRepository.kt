package com.robotutor.nexora.context.device.domain.repository

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import reactor.core.publisher.Mono

interface DeviceRepository {
    fun save(device: DeviceAggregate): Mono<DeviceAggregate>
    fun findByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate>
//    fun findAllByPremisesIdAndDeviceIdsIn(premisesId: PremisesId, deviceIds: List<DeviceId>): Flux<DeviceAggregate>
//    fun findByPremisesIdAndDeviceId(premisesId: PremisesId, deviceId: DeviceId): Mono<DeviceAggregate>
    fun deleteByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate>
}