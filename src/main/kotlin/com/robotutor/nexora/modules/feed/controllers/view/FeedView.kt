package com.robotutor.nexora.modules.feed.controllers.view

import com.robotutor.nexora.modules.feed.models.Feed
import com.robotutor.nexora.modules.feed.models.FeedId
import com.robotutor.nexora.modules.feed.models.FeedType
import com.robotutor.nexora.modules.premises.models.PremisesId
import jakarta.validation.constraints.NotBlank

data class FeedRequest(
    @field:NotBlank(message = "Name is required")
    val name: String,
    val type: FeedType
)

data class FeedValueRequest(val value: Number)

data class FeedView(
    val feedId: FeedId,
    val premisesId: PremisesId,
    val name: String,
    val value: Number,
    val type: FeedType,
) {
    companion object {
        fun from(feed: Feed): FeedView {
            return FeedView(
                feedId = feed.feedId,
                premisesId = feed.premisesId,
                name = feed.name,
                value = feed.value,
                type = feed.type
            )
        }
    }
}
