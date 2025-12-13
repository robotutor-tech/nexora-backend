package com.robotutor.nexora.modules.device.application.facade

import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.shared.domain.vo.ActorData
import reactor.core.publisher.Mono

interface ActorFacade {
    fun registerDeviceActor(device: Device): Mono<ActorData>
}