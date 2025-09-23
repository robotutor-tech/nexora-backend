package com.robotutor.nexora.modules.feed.domain.event

import com.robotutor.nexora.shared.domain.model.FeedId

data class FeedValueUpdatedEvent(
    val feedId: FeedId,
    val value: Int
) : FeedEvent("value.updated")
