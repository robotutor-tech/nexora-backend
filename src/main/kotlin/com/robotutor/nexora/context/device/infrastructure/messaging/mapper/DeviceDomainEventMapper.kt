package com.robotutor.nexora.context.device.infrastructure.messaging.mapper

import com.robotutor.nexora.context.device.domain.event.DeviceCommissionedEvent
import com.robotutor.nexora.context.device.domain.event.DeviceDomainEvent
import com.robotutor.nexora.context.device.domain.event.DeviceMetadataUpdatedEvent
import com.robotutor.nexora.context.device.domain.event.DeviceRegisteredEvent
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceCommissionedEventMessage
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceMetadataUpdatedEventMessage
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceRegisteredEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object DeviceDomainEventMapper : EventMapper<DeviceDomainEvent> {
    override fun toEventMessage(event: DeviceDomainEvent): EventMessage {
        return when (event) {
            is DeviceRegisteredEvent -> toDeviceRegisteredEventMessage(event)
            is DeviceCommissionedEvent -> toDeviceCommissionedEventMessage(event)
            is DeviceMetadataUpdatedEvent -> toDeviceMetadataUpdatedEventMessage(event)
        }
    }

    private fun toDeviceMetadataUpdatedEventMessage(event: DeviceMetadataUpdatedEvent): DeviceMetadataUpdatedEventMessage {
        return DeviceMetadataUpdatedEventMessage(
            deviceId = event.deviceId.value,
            modelNo = event.metadata.modelNo.value,
            serialNo = event.metadata.serialNo.value
        )
    }

    private fun toDeviceCommissionedEventMessage(event: DeviceCommissionedEvent): DeviceCommissionedEventMessage {
        return DeviceCommissionedEventMessage(
            deviceId = event.deviceId.value,
            premisesId = event.premisesId.value,
            accountId = event.accountId.value
        )
    }

    private fun toDeviceRegisteredEventMessage(event: DeviceRegisteredEvent): DeviceRegisteredEventMessage {
        return DeviceRegisteredEventMessage(
            deviceId = event.deviceId.value,
            premisesId = event.premisesId.value,
            name = event.name.value
        )
    }
}