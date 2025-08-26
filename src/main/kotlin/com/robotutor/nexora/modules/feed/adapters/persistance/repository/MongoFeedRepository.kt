package com.robotutor.nexora.modules.feed.adapters.persistence.repository

import com.robotutor.nexora.modules.feed.domain.model.Feed
import com.robotutor.nexora.modules.feed.domain.repository.FeedRepository
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoFeedRepository(private val feedDocumentRepository: FeedDocumentRepository) : FeedRepository {
    override fun findAllByPremisesIdAndFeedIdIn(premisesId: PremisesId, feedIds: List<FeedId>): Flux<Feed> {
        return feedDocumentRepository.findAllByPremisesIdAndFeedIdIn(premisesId.value, feedIds.map { it.value })
            .map { it.toDomainModel() }
    }

    override fun save(feed: Feed): Mono<Feed> {
        TODO("Not yet implemented")
    }
}