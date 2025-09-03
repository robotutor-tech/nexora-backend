package com.robotutor.nexora.modules.device.infrastructure.messaging.mapper

import com.robotutor.nexora.modules.device.domain.event.DeviceCreatedEvent
import com.robotutor.nexora.modules.device.domain.event.DeviceEvent
import com.robotutor.nexora.modules.device.infrastructure.messaging.message.DeviceCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage
import org.springframework.stereotype.Component

@Component
class DeviceEventMapper : EventMapper<DeviceEvent> {
    override fun toEventMessage(event: DeviceEvent): EventMessage {
        return when (event) {
            is DeviceCreatedEvent -> toDeviceCreatedEventMessage(event)
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