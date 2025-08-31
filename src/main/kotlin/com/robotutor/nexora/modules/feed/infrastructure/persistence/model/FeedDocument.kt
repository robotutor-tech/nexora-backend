package com.robotutor.nexora.modules.feed.infrastructure.persistence.model

import com.robotutor.nexora.modules.feed.domain.model.Feed
import com.robotutor.nexora.modules.feed.domain.model.FeedType
import com.robotutor.nexora.shared.infrastructure.persistence.model.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val FEED_COLLECTION = "feeds"

@TypeAlias("Feed")
@Document(FEED_COLLECTION)
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
) : MongoDocument<Feed>

