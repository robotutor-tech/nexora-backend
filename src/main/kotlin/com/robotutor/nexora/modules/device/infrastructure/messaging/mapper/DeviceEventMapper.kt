package com.robotutor.nexora.modules.device.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.device.domain.event.DeviceCreatedEvent
import com.robotutor.nexora.modules.device.infrastructure.messaging.message.DeviceCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage
import org.springframework.stereotype.Component

@Component
class DeviceEventMapper : EventMapper {
    override fun toEventMessage(event: DomainEvent): EventMessage {
        return when (event) {
            is DeviceCreatedEvent -> toDeviceCreatedEventMessage(event)

            else -> throw IllegalArgumentException("Unsupported event type: ${event::class.java}")
        }
    }

    private fun toDeviceCreatedEventMessage(event: DeviceCreatedEvent): DeviceCreatedEventMessage {
        return DeviceCreatedEventMessage(
            deviceId = event.deviceId.value,
            modelNo = event.modelNo.value,
            zoneId = event.zoneId.value
        )
    }
}