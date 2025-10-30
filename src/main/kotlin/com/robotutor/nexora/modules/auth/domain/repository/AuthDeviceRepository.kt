package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.entity.AuthDevice
import com.robotutor.nexora.modules.auth.domain.entity.DeviceSecret
import com.robotutor.nexora.shared.domain.model.DeviceId
import reactor.core.publisher.Mono

interface AuthDeviceRepository {
    fun save(authDevice: AuthDevice): Mono<AuthDevice>
    fun findByDeviceIdAndDeviceSecret(deviceId: DeviceId, deviceSecret: DeviceSecret): Mono<AuthDevice>
}