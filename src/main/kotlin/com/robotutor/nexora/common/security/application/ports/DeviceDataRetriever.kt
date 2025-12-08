package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.domain.model.DeviceData
import com.robotutor.nexora.shared.domain.model.DeviceId
import reactor.core.publisher.Mono

interface DeviceDataRetriever {
    fun getDeviceData(deviceId: DeviceId): Mono<DeviceData>
}