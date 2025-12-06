package com.robotutor.nexora.modules.device.infrastructure.messaging.message

import com.robotutor.nexora.shared.infrastructure.messaging.message.EventMessage

data class DeviceCreatedEventMessage(val deviceId: String, val modelNo: String, val zoneId: String) : EventMessage("device.created")
