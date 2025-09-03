package com.robotutor.nexora.modules.feed.domain.event

import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId

data class DeviceFeedsCreatedEvent(
    val deviceId: DeviceId, val feedIds: List<FeedId>
) : FeedEvent("device.feeds-created")
