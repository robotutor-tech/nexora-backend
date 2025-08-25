package com.robotutor.nexora.modules.feed.interfaces.controller.dto

import com.robotutor.nexora.modules.feed.domain.model.FeedType

data class FeedResponse(
    val feedId: String,
    val premisesId: String,
    val name: String,
    val value: Number,
    val type: FeedType,
)