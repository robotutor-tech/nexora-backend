package com.robotutor.nexora.context.device.interfaces.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class CompensateDeviceRegistrationMessage(val deviceId: String) : EventMessage()