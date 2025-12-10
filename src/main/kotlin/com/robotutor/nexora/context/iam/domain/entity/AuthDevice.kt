package com.robotutor.nexora.context.iam.domain.entity

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.RoleId
import java.time.Instant

data class AuthDevice(
    val deviceId: DeviceId,
    val actorId: ActorId,
    val roleId: RoleId,
    val secret: DeviceSecret,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    val version: Long = 0
) : AggregateRoot<AuthDevice, DeviceId, IAMEvent>(deviceId) {
    companion object {
        fun register(deviceId: DeviceId, actorId: ActorId, roleId: RoleId): AuthDevice {
            val secret = DeviceSecret.generate()
            val device = AuthDevice(deviceId = deviceId, actorId = actorId, roleId = roleId, secret = secret)
//            device.addEvent(AuthDeviceRegisteredEvent(device.deviceId))
            return device
        }
    }
}


data class DeviceSecret(val value: String) {
    companion object {
        fun generate(): DeviceSecret {
            val value = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 32)
            return DeviceSecret(value = value)
        }
    }
}
