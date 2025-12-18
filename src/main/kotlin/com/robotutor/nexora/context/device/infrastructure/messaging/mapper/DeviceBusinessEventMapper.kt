package com.robotutor.nexora.context.device.infrastructure.messaging.mapper

import com.robotutor.nexora.context.device.domain.event.DeviceBusinessEvent
import com.robotutor.nexora.context.device.domain.event.DeviceRegistrationCompensatedEvent
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceRegistrationCompensatedEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object DeviceBusinessEventMapper : EventMapper<DeviceBusinessEvent> {
    override fun toEventMessage(event: DeviceBusinessEvent): EventMessage {
        return when (event) {
            is DeviceRegistrationCompensatedEvent -> toDeviceRegistrationCompensatedEventMessage(event)
        }
    }

    private fun toDeviceRegistrationCompensatedEventMessage(event: DeviceRegistrationCompensatedEvent): DeviceRegistrationCompensatedEventMessage {
        return DeviceRegistrationCompensatedEventMessage(deviceId = event.deviceId.value)
    }
}