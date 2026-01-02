package com.robotutor.nexora.module.feed.domain.event

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.feed.domain.aggregate.FeedType
import com.robotutor.nexora.module.feed.domain.vo.FeedValueRange
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.FeedId

sealed interface FeedEvent : Event

data class FeedRegisteredEvent(
    val feedId: FeedId,
    val deviceId: DeviceId,
    val type: FeedType,
    val range: FeedValueRange,
) : FeedEvent