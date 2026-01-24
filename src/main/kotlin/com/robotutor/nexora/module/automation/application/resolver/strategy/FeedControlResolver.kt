package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.application.facade.FeedFacade
import com.robotutor.nexora.module.automation.domain.vo.component.FeedControl
import com.robotutor.nexora.module.automation.domain.vo.component.data.FeedControlData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeedControlResolver(private val feedFacade: FeedFacade) : ComponentResolver<FeedControl, FeedControlData> {
    override fun resolve(component: FeedControl): Mono<FeedControlData> {
        return feedFacade.getFeedById(component.feedId)
            .map { FeedControlData(feedId = it.feedId, value = it.value) }
    }
}