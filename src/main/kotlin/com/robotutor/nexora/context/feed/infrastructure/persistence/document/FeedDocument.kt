package com.robotutor.nexora.context.feed.infrastructure.persistence.document

import com.robotutor.nexora.context.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.feed.domain.aggregate.FeedType
import com.robotutor.nexora.context.feed.domain.vo.FeedMode
import com.robotutor.nexora.common.persistence.mongo.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val FEED_COLLECTION = "feeds"

@Document(FEED_COLLECTION)
@TypeAlias("Feed")
data class FeedDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val feedId: String,
    val deviceId: String,
    val premisesId: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val type: FeedType,
    val min: Int,
    val max: Int,
    val mode: FeedMode,
    val value: Int,
    @Version
    val version: Long? = null
) : MongoDocument<FeedAggregate>

