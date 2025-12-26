package com.robotutor.nexora.context.device.domain.repository

import com.robotutor.nexora.context.device.domain.aggregate.FeedAggregate
import com.robotutor.nexora.shared.domain.specification.Specification
import reactor.core.publisher.Flux

interface FeedRepository {
    fun saveAll(feeds: List<FeedAggregate>): Flux<FeedAggregate>
    fun findAll(specification: Specification<FeedAggregate>): Flux<FeedAggregate>
}