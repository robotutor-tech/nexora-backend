package com.robotutor.nexora.modules.automation.application.facade

import com.robotutor.nexora.shared.domain.vo.FeedId
import reactor.core.publisher.Mono

interface FeedFacade {
    fun getFeedById(feedId: FeedId): Mono<Any>
}