package com.robotutor.nexora.modules.device.interfaces.messaging.dto

data class UpdateFeedsDto(
    val deviceId: String,
    val feeds: List<String>
)
