package com.robotutor.nexora.module.device.infrastructure.messaging.message

import com.robotutor.nexora.shared.message.config.EventName
import com.robotutor.nexora.shared.message.message.EventMessage

sealed interface DeviceEventMessage : EventMessage

data class DeviceRegistrationCompensatedEventMessage(val deviceId: String) :
    DeviceEventMessage {
    override val eventName: EventName = EventName.REGISTRATION_COMPLETED
}

data class DeviceActivatedEventMessage(val deviceId: String, val premisesId: String) : DeviceEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}

data class DeviceCommissionedEventMessage(val deviceId: String, val premisesId: String, val accountId: String) :
    DeviceEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}

data class DeviceRegisteredEventMessage(val deviceId: String, val premisesId: String, val name: String) :
    DeviceEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}

data class DeviceRegistrationFailedEventMessage(val accountId: String) :
    DeviceEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}

data class DeviceMetadataUpdatedEventMessage(val deviceId: String, val modelNo: String, val serialNo: String) :
    DeviceEventMessage {
    override val eventName: EventName = EventName.ACCOUNT_REGISTRATION_FAILED
}
