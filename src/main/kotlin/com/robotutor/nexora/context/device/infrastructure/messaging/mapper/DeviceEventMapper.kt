package com.robotutor.nexora.context.device.infrastructure.messaging.mapper

import com.robotutor.nexora.context.device.domain.event.*
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceCommissionedEventMessage
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceMetadataUpdatedEventMessage
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceRegisteredEventMessage
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceRegistrationCompensatedEventMessage
import com.robotutor.nexora.context.device.infrastructure.messaging.message.DeviceRegistrationFailedEventMessage
import com.robotutor.nexora.context.device.infrastructure.messaging.message.FeedRegisteredEventMessage
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

object DeviceEventMapper : EventMapper<DeviceEvent> {
    override fun toEventMessage(event: DeviceEvent): EventMessage {
        return when (event) {
            is DeviceRegistrationCompensatedEvent -> toDeviceRegistrationCompensatedEventMessage(event)
            is DeviceRegisteredEvent -> toDeviceRegisteredEventMessage(event)
            is DeviceRegistrationFailedEvent -> toDeviceRegistrationFailedEventMessage(event)
            is DeviceCommissionedEvent -> toDeviceCommissionedEventMessage(event)
            is DeviceMetadataUpdatedEvent -> toDeviceMetadataUpdatedEventMessage(event)
            is FeedRegisteredEvent -> toFeedRegisteredEventMessage(event)
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

    private fun toFeedRegisteredEventMessage(event: FeedRegisteredEvent): FeedRegisteredEventMessage {
        return FeedRegisteredEventMessage(deviceId = event.deviceId.value, feedId = event.feedId.value)
    }
}