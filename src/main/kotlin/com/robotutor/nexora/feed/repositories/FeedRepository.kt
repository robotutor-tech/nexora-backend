package com.robotutor.nexora.feed.repositories

import com.robotutor.nexora.feed.models.Feed
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.premises.models.PremisesId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface FeedRepository : ReactiveCrudRepository<Feed, FeedId> {
    fun findAllByPremisesIdAndFeedIdIn(premisesId: PremisesId, feedIds: List<FeedId>): Flux<Feed>
    fun findByFeedIdAndPremisesId(feedId: FeedId, premisesId: PremisesId): Mono<Feed>
}
