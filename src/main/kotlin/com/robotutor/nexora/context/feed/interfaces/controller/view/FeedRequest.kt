package com.robotutor.nexora.context.feed.interfaces.controller.view

import com.robotutor.nexora.context.feed.domain.aggregate.FeedType
import com.robotutor.nexora.context.feed.domain.vo.FeedMode
import java.time.Instant

data class FeedResponse(
    val feedId: String,
    val deviceId: String,
    val premisesId: String,
    val type: FeedType,
    val value: Int,
    val range: FeedValueRangeResponse,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class FeedValueRangeResponse(
    val mode: FeedMode,
    val min: Int,
    val max: Int
)

