package com.robotutor.nexora.module.automation.infrastructure.facade

import com.robotutor.nexora.module.automation.application.facade.FeedFacade
import com.robotutor.nexora.shared.domain.vo.FeedId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("AutomationFeedFacade")
class FeedApiFacade() : FeedFacade {
    override fun getFeedById(feedId: FeedId): Mono<Any> {
        return Mono.empty()
//        return ContextDataResolver.getActorData().flatMap { Actor ->
//            feedController.getFeed(feedId.value, Actor)
//        }
    }
}