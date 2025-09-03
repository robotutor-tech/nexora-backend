package com.robotutor.nexora.modules.feed.interfaces.controller.mapper

import com.robotutor.nexora.modules.feed.domain.entity.Feed
import com.robotutor.nexora.modules.feed.interfaces.controller.dto.FeedResponse

class FeedMapper {
    companion object {
        fun toFeedResponse(feed: Feed): FeedResponse {
            return FeedResponse(
                feedId = feed.feedId.value,
                premisesId = feed.premisesId.value,
                name = feed.name.value,
                value = feed.value,
                type = feed.type
            )

        }
    }
}