package com.robotutor.nexora.modules.feed.interfaces.controller.mapper

import com.robotutor.nexora.modules.feed.application.command.FeedValueUpdateCommand
import com.robotutor.nexora.modules.feed.domain.entity.Feed
import com.robotutor.nexora.modules.feed.interfaces.controller.dto.FeedResponse
import com.robotutor.nexora.modules.feed.interfaces.controller.dto.FeedValueRequest
import com.robotutor.nexora.shared.domain.vo.FeedId

object FeedMapper {
    fun toFeedResponse(feed: Feed): FeedResponse {
        return FeedResponse(
            feedId = feed.feedId.value,
            premisesId = feed.premisesId.value,
            name = feed.name.value,
            value = feed.value,
            type = feed.type
        )

    }

    fun toFeedValueUpdateCommand(feedId: String, feedRequest: FeedValueRequest): FeedValueUpdateCommand {
        return FeedValueUpdateCommand(FeedId(feedId), feedRequest.value)
    }
}