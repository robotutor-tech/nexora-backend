package com.robotutor.nexora.automation.gateways

import com.robotutor.nexora.automation.config.AutomationConfig
import com.robotutor.nexora.feed.controllers.view.FeedView
import com.robotutor.nexora.feed.models.FeedId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FeedGateway(private val webClient: WebClientWrapper, private val automationConfig: AutomationConfig) {

    fun getFeedByFeedId(feedId: FeedId): Mono<FeedView> {
        return webClient.get(
            baseUrl = automationConfig.feedServiceBaseUrl,
            path = automationConfig.feedByIdPath,
            uriVariables = mapOf("feedId" to feedId),
            returnType = FeedView::class.java
        )
    }

}