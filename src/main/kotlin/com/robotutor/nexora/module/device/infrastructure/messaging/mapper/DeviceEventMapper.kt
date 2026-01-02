package com.robotutor.nexora.module.device.infrastructure.messaging.mapper

import com.robotutor.nexora.module.device.domain.event.*
import com.robotutor.nexora.module.device.infrastructure.messaging.message.*
import com.robotutor.nexora.common.messaging.mapper.EventMapper
import com.robotutor.nexora.common.messaging.message.EventMessage

object DeviceEventMapper : EventMapper<DeviceEvent> {
    override fun toEventMessage(event: DeviceEvent): EventMessage {
        return when (event) {
            is DeviceRegistrationCompensatedEvent -> toDeviceRegistrationCompensatedEventMessage(event)
            is DeviceRegisteredEvent -> toDeviceRegisteredEventMessage(event)
            is DeviceRegistrationFailedEvent -> toDeviceRegistrationFailedEventMessage(event)
            is DeviceCommissionedEvent -> toDeviceCommissionedEventMessage(event)
            is DeviceMetadataUpdatedEvent -> toDeviceMetadataUpdatedEventMessage(event)
            is DeviceActivatedEvent -> DeviceActivatedEventMessage(event.deviceId.value, event.premisesId.value)
        }
    }

    private fun toDeviceRegistrationCompensatedEventMessage(event: DeviceRegistrationCompensatedEvent): DeviceRegistrationCompensatedEventMessage {
        return DeviceRegistrationCompensatedEventMessage(deviceId = event.deviceId.value)
    }

    private fun toDeviceRegistrationFailedEventMessage(event: DeviceRegistrationFailedEvent): DeviceRegistrationFailedEventMessage {
        return DeviceRegistrationFailedEventMessage(event.accountId.value)
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
            accountId = event.actorId.value
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