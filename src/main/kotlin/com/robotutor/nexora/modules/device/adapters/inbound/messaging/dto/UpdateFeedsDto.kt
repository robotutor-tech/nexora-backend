package com.robotutor.nexora.modules.device.adapters.inbound.messaging.dto

data class UpdateFeedsDto(
    val deviceId: String,
    val feeds: List<String>
)
