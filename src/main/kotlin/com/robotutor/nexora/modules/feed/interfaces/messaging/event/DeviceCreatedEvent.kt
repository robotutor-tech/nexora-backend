package com.robotutor.nexora.modules.feed.interfaces.messaging.event

data class DeviceCreatedEvent(
    val deviceId: String,
    val deviceType: String,
    val createdAt: String
)


