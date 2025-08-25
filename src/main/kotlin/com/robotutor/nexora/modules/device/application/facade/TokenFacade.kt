package com.robotutor.nexora.modules.device.application.facade

import com.robotutor.nexora.modules.device.application.facade.dto.DeviceTokens
import com.robotutor.nexora.shared.domain.model.DeviceData
import reactor.core.publisher.Mono

interface TokenFacade {
    fun generateDeviceToken(deviceData: DeviceData): Mono<DeviceTokens>
}