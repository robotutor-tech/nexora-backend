package com.robotutor.nexora.modules.feed.domain.entity

import com.robotutor.nexora.modules.feed.application.command.CreateFeedCommand
import com.robotutor.nexora.modules.feed.domain.event.FeedCreatedEvent
import com.robotutor.nexora.modules.feed.domain.event.FeedEvent
import com.robotutor.nexora.shared.domain.event.DomainAggregate
import com.robotutor.nexora.shared.domain.model.*
import java.time.Instant

data class Feed(
    val feedId: FeedId,
    val premisesId: PremisesId,
    val name: Name,
    var value: Number = 0,
    val type: FeedType,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    val version: Long? = null
) : DomainAggregate<FeedEvent>() {
    companion object {
        fun create(feedId: FeedId, zoneId: ZoneId, createFeedCommand: CreateFeedCommand, actorData: ActorData): Feed {
            val feed = Feed(
                feedId = feedId,
                premisesId = actorData.premisesId,
                name = createFeedCommand.name,
                type = createFeedCommand.type
            )
            feed.addDomainEvent(
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

