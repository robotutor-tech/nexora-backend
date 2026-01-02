package com.robotutor.nexora.module.feed.domain.aggregate

import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.feed.domain.event.FeedEvent
import com.robotutor.nexora.module.feed.domain.event.FeedRegisteredEvent
import com.robotutor.nexora.module.feed.domain.vo.FeedValueRange
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

class FeedAggregate private constructor(
    val feedId: FeedId,
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val type: FeedType,
    val createdAt: Instant,
    val range: FeedValueRange,
    private var value: Int,
    private var updatedAt: Instant,
) : AggregateRoot<FeedAggregate, FeedId, FeedEvent>(feedId) {

    fun getValue(): Int = value
    fun getUpdatedAt(): Instant = updatedAt

    companion object {
        fun create(
            feedId: FeedId,
            deviceId: DeviceId,
            premisesId: PremisesId,
            type: FeedType = FeedType.ACTUATOR,
            range: FeedValueRange = FeedValueRange(),
            createdAt: Instant = Instant.now(),
            value: Int = 0,
            updatedAt: Instant = Instant.now(),
        ): FeedAggregate {
            return FeedAggregate(feedId, deviceId, premisesId, type, createdAt, range, value, updatedAt)
        }

        fun register(
            deviceId: DeviceId,
            premisesId: PremisesId,
            type: FeedType,
            range: FeedValueRange,
        ): FeedAggregate {
            val feed = create(FeedId.generate(), deviceId, premisesId, type, range)
            feed.addEvent(FeedRegisteredEvent(feed.feedId, feed.deviceId, feed.type, feed.range))
            return feed
        }
    }
}

enum class FeedType {
    SENSOR,
    ACTUATOR,
}

