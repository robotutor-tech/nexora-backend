package com.robotutor.nexora.modules.feed.domain.event

import com.robotutor.nexora.shared.domain.vo.FeedId

data class FeedValueUpdatedEvent(
    val feedId: FeedId,
    val value: Int
) : FeedEvent
