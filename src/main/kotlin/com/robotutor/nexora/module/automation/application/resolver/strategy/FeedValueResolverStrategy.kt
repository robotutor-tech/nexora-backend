package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.application.facade.FeedFacade
import com.robotutor.nexora.module.automation.application.resolver.ResolverStrategy
import com.robotutor.nexora.module.automation.domain.vo.component.FeedValue
import com.robotutor.nexora.module.automation.domain.vo.component.data.FeedValueData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedValueResolverStrategy(private val feedFacade: FeedFacade) : ResolverStrategy<FeedValue, FeedValueData> {
    override fun resolve(component: FeedValue): Mono<FeedValueData> {
        return feedFacade.getFeedById(component.feedId)
            .map { FeedValueData(feedId = it.feedId, value = it.value) }
    }
}