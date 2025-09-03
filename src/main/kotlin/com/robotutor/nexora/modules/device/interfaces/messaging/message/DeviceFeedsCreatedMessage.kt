package com.robotutor.nexora.modules.device.interfaces.messaging.message

data class DeviceFeedsCreatedMessage(
    val deviceId: String,
    val feedIds: List<String>
)
