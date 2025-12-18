package com.robotutor.nexora.context.device.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class DeviceCommissionedEventMessage(val deviceId: String, val premisesId: String, val accountId: String) :
    EventMessage("device.commissioned")
