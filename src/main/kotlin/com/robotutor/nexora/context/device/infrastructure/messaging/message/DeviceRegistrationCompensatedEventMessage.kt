package com.robotutor.nexora.context.device.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class DeviceRegistrationCompensatedEventMessage(val deviceId: String) :
    EventMessage("device.registration.compensated")
