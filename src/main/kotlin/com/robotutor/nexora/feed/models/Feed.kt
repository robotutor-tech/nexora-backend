package com.robotutor.nexora.feed.models

import com.robotutor.nexora.feed.controllers.view.FeedRequest
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.PremisesActorData
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

const val Feed_COLLECTION = "feeds"

@TypeAlias("Feed")
@Document(Feed_COLLECTION)
data class Feed(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val feedId: FeedId,
    val premisesId: PremisesId,
    val name: String,
    val value: Number = 0,
    val type: FeedType,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun from(feedId: FeedId, feedRequest: FeedRequest, premisesActorData: PremisesActorData): Feed {
            return Feed(
                feedId = feedId,
                premisesId = premisesActorData.premisesId,
                name = feedRequest.name,
                type = feedRequest.type,
            )
        }
    }
}

enum class FeedType {
    SENSOR,
    ACTUATOR,
    VIRTUAL
}

typealias FeedId = String