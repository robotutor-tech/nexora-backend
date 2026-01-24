package com.robotutor.nexora.module.feed.domain.repository

import com.robotutor.nexora.module.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.shared.domain.specification.Specification
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FeedRepository {
    fun save(feed: FeedAggregate): Mono<FeedAggregate>
    fun saveAll(feeds: List<FeedAggregate>): Flux<FeedAggregate>
    fun findAll(specification: Specification<FeedAggregate>): Flux<FeedAggregate>
    fun find(specification: Specification<FeedAggregate>): Mono<FeedAggregate>
}

