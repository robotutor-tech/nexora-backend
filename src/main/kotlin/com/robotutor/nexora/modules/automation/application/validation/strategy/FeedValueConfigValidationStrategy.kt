package com.robotutor.nexora.modules.automation.application.validation.strategy

import com.robotutor.nexora.modules.automation.application.facade.FeedFacade
import com.robotutor.nexora.modules.automation.domain.entity.config.FeedValueConfig
import com.robotutor.nexora.shared.domain.model.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedValueConfigValidationStrategy(private val feedFacade: FeedFacade) : ValidationStrategy<FeedValueConfig> {
    override fun validate(config: FeedValueConfig, actorData: ActorData): Mono<FeedValueConfig> {
        return feedFacade.getFeedById(config.feedId).map { config }
    }
}