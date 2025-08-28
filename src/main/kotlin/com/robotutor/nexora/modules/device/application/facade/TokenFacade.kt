package com.robotutor.nexora.modules.device.application.facade

import com.robotutor.nexora.modules.device.application.facade.dto.DeviceTokens
import com.robotutor.nexora.shared.domain.model.ActorData
import reactor.core.publisher.Mono

interface TokenFacade {
    fun generateDeviceToken(actorData: ActorData): Mono<DeviceTokens>
}