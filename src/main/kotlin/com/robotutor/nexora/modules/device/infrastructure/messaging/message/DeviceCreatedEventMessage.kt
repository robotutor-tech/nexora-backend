package com.robotutor.nexora.modules.device.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class DeviceCreatedEventMessage(val deviceId: String, val modelNo: String, val zoneId: String) : EventMessage
