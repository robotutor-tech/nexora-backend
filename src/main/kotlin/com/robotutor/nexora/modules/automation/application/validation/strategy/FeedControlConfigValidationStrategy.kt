package com.robotutor.nexora.modules.automation.application.validation.strategy

import com.robotutor.nexora.modules.automation.application.facade.FeedFacade
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedControlConfig
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedControlConfigValidationStrategy(private val feedFacade: FeedFacade) : ValidationStrategy<FeedControlConfig> {
    override fun validate(config: FeedControlConfig, actorData: ActorData): Mono<FeedControlConfig> {
        return feedFacade.getFeedById(config.feedId).map { config }
    }
}