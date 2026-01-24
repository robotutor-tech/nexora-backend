package com.robotutor.nexora.module.automation.infrastructure.facade

import com.robotutor.nexora.common.webclient.WebClientWrapper
import com.robotutor.nexora.module.automation.application.facade.FeedFacade
import com.robotutor.nexora.module.automation.application.facade.view.FeedResponse
import com.robotutor.nexora.module.automation.infrastructure.facade.config.AutomationConfig
import com.robotutor.nexora.module.automation.infrastructure.facade.view.FeedFacadeResponse
import com.robotutor.nexora.shared.domain.vo.FeedId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("AutomationFeedFacade")
class FeedApiFacade(
    private val webClient: WebClientWrapper,
    private val automationConfig: AutomationConfig
) : FeedFacade {
    override fun getFeedById(feedId: FeedId): Mono<FeedResponse> {
        return webClient.get(
            baseUrl = automationConfig.feedBaseUrl,
            path = automationConfig.feedPath,
            uriVariables = mapOf("feedId" to feedId.value),
            returnType = FeedFacadeResponse::class.java
        )
            .map {
                FeedResponse(FeedId(it.feedId), it.value)
            }
    }
}