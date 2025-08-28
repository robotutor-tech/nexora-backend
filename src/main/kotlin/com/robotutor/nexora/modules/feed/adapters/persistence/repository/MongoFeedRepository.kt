package com.robotutor.nexora.modules.feed.adapters.persistence.repository

import com.robotutor.nexora.modules.feed.adapters.persistence.mapper.FeedDocumentMapper
import com.robotutor.nexora.modules.feed.adapters.persistence.model.FeedDocument
import com.robotutor.nexora.modules.feed.domain.model.Feed
import com.robotutor.nexora.modules.feed.domain.repository.FeedRepository
import com.robotutor.nexora.shared.adapters.persistence.repository.MongoRepository
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class MongoFeedRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Feed, FeedDocument>(mongoTemplate, FeedDocument::class.java, FeedDocumentMapper()),
    FeedRepository {
    override fun save(feed: Feed): Mono<Feed> {
        val query = Query(Criteria.where("feedId").`is`(feed.feedId.value))
        return this.findAndReplace(query, feed)
    }

    override fun findAllByPremisesIdAndFeedIdIn(premisesId: PremisesId, feedIds: List<FeedId>): Flux<Feed> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("feedId").`in`(feedIds.map { it.value })
        )
        return this.findAll(query)
    }
}
