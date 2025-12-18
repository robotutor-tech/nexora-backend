package com.robotutor.nexora.context.device.application.facade

import com.robotutor.nexora.context.device.application.facade.dto.AuthDevice
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.vo.ActorData
import reactor.core.publisher.Mono

interface AuthDeviceFacade {
    fun register(device: DeviceAggregate, actorData: ActorData): Mono<AuthDevice>
}