package com.robotutor.nexora.modules.feed.domain.entity

import com.robotutor.nexora.modules.feed.application.command.CreateFeedCommand
import com.robotutor.nexora.modules.feed.domain.event.FeedCreatedEvent
import com.robotutor.nexora.modules.feed.domain.event.FeedEvent
import com.robotutor.nexora.modules.feed.domain.event.FeedValueUpdatedEvent
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ActorData

import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId
import java.time.Instant

data class Feed(
    val feedId: FeedId,
    val premisesId: PremisesId,
    val name: Name,
    var value: Int = 0,
    val type: FeedType,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
) : AggregateRoot<Feed, FeedId, FeedEvent>(feedId) {

    fun updateValue(newValue: Int): Feed {
        this.value = newValue
        this.addEvent(FeedValueUpdatedEvent(feedId, newValue))
        return this
    }

    companion object {
        fun create(feedId: FeedId, zoneId: ZoneId, createFeedCommand: CreateFeedCommand, actorData: ActorData): Feed {
            val feed = Feed(
                feedId = feedId,
                premisesId = actorData.premisesId,
                name = createFeedCommand.name,
                type = createFeedCommand.type
            )
            feed.addEvent(
                FeedCreatedEvent(
                    feedId = feed.feedId,
                    name = feed.name,
                    type = feed.type,
                    widgetType = createFeedCommand.widgetType,
                    zoneId = zoneId
                )
            )
            return feed
        }
    }
}

enum class FeedType {
    SENSOR,
    ACTUATOR,
    VIRTUAL
}

