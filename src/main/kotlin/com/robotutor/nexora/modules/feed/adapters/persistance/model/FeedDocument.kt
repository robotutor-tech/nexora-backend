package com.robotutor.nexora.modules.feed.adapters.persistance.model


import com.robotutor.nexora.modules.feed.domain.model.Feed
import com.robotutor.nexora.modules.feed.domain.model.FeedType
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val Feed_COLLECTION = "feeds"

@TypeAlias("Feed")
@Document(Feed_COLLECTION)
data class FeedDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val feedId: String,
    @Indexed
    val premisesId: String,
    val name: String,
    val value: Number = 0,
    val type: FeedType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
) {
    fun toDomainModel(): Feed {
        return Feed(
            feedId = FeedId(feedId),
            premisesId = PremisesId(premisesId),
            name = Name(name),
            value = value,
            type = type,
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version
        )
    }

    companion object {
        fun from(feed: Feed): FeedDocument {
            return FeedDocument(
                feedId = feed.feedId.value,
                premisesId = feed.premisesId.value,
                name = feed.name.value,
                value = feed.value,
                type = feed.type,
                createdAt = feed.createdAt,
                updatedAt = feed.updatedAt,
                version = feed.version,
            )
        }
    }
}