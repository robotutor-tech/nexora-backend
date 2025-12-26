package com.robotutor.nexora.context.device.infrastructure.facade.view

import java.time.Instant

data class FeedResponse(
    val feedId: String,
    val deviceId: String,
    val premisesId: String,
    val value: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
)


