package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.domain.model.DeviceData
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.domain.model.UserId
import reactor.core.publisher.Mono

interface DeviceDataRetriever {
    fun getDeviceData(deviceId: DeviceId): Mono<DeviceData>
}