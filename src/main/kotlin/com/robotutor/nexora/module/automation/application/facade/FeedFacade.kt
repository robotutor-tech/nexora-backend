package com.robotutor.nexora.module.automation.application.facade

import com.robotutor.nexora.module.automation.application.facade.view.FeedResponse
import com.robotutor.nexora.shared.domain.vo.FeedId
import reactor.core.publisher.Mono

interface FeedFacade {
    fun getFeedById(feedId: FeedId): Mono<FeedResponse>
}