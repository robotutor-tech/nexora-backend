package com.robotutor.nexora.context.device.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class DeviceRegisteredEventMessage(val deviceId: String, val premisesId: String, val name: String) :
    EventMessage("device.registered")
