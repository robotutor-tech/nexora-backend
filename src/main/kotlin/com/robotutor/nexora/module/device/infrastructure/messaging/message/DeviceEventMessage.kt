package com.robotutor.nexora.module.device.infrastructure.messaging.message

import com.robotutor.nexora.common.messaging.message.EventMessage

sealed class DeviceEventMessage(name: String) : EventMessage("device.$name")

data class DeviceRegistrationCompensatedEventMessage(val deviceId: String) :
    DeviceEventMessage("registration.compensated")

data class DeviceActivatedEventMessage(val deviceId: String, val premisesId: String) : DeviceEventMessage("activated")

data class DeviceCommissionedEventMessage(val deviceId: String, val premisesId: String, val accountId: String) :
    DeviceEventMessage("commissioned")

data class DeviceRegisteredEventMessage(val deviceId: String, val premisesId: String, val name: String) :
    DeviceEventMessage("registered")

data class DeviceRegistrationFailedEventMessage(val accountId: String) :
    DeviceEventMessage("registration.failed")

data class DeviceMetadataUpdatedEventMessage(val deviceId: String, val modelNo: String, val serialNo: String) :
    DeviceEventMessage("metadata.updated")
