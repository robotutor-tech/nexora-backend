package com.robotutor.nexora.modules.device.application.facade

import com.robotutor.nexora.modules.device.application.facade.dto.AuthDevice
import com.robotutor.nexora.modules.device.domain.entity.Device
import com.robotutor.nexora.shared.domain.model.ActorData
import reactor.core.publisher.Mono

interface AuthDeviceFacade {
    fun register(device: Device, actorData: ActorData): Mono<AuthDevice>
}