package com.robotutor.nexora.modules.feed.domain.repository

import com.robotutor.nexora.modules.feed.domain.model.Feed
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FeedRepository {
    fun findAllByPremisesIdAndFeedIdIn(premisesId: PremisesId, feedIds: List<FeedId>): Flux<Feed>
    fun save(feed: Feed): Mono<Feed>
}