package com.robotutor.nexora.context.device.domain.repository

import com.robotutor.nexora.context.device.domain.aggregate.FeedAggregate
import reactor.core.publisher.Flux

interface FeedRepository {
    fun saveAll(feeds: List<FeedAggregate>): Flux<FeedAggregate>
}