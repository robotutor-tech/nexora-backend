package com.robotutor.nexora.modules.automation.application.facade

import com.robotutor.nexora.shared.domain.model.FeedId
import reactor.core.publisher.Mono

interface FeedFacade {
    fun getFeedById(feedId: FeedId): Mono<Any>
}