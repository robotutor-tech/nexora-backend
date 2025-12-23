package com.robotutor.nexora.modules.automation.infrastructure.facade

import com.robotutor.nexora.modules.automation.application.facade.FeedFacade
import com.robotutor.nexora.modules.feed.interfaces.controller.FeedController
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.vo.FeedId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedApiFacade(private val feedController: FeedController) : FeedFacade {
    override fun getFeedById(feedId: FeedId): Mono<Any> {

        return ContextDataResolver.getActorData().flatMap { actorData ->
            feedController.getFeed(feedId.value, actorData)
        }
    }
}