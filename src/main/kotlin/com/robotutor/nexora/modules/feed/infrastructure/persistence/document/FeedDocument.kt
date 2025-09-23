package com.robotutor.nexora.modules.feed.infrastructure.persistence.document

import com.robotutor.nexora.modules.feed.domain.entity.Feed
import com.robotutor.nexora.modules.feed.domain.entity.FeedType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
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
    val value: Int,
    val type: FeedType,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long?
) : MongoDocument<Feed>

