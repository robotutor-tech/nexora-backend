package com.robotutor.nexora.context.device.domain.repository

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.domain.specification.Specification
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface DeviceRepository {
    fun save(device: DeviceAggregate): Mono<DeviceAggregate>
    fun findByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate>
    fun deleteByDeviceId(deviceId: DeviceId): Mono<DeviceAggregate>
    fun findAll(specification: Specification<DeviceAggregate>): Flux<DeviceAggregate>
}