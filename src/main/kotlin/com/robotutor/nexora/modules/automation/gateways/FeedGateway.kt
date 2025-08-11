package com.robotutor.nexora.modules.automation.gateways

import com.robotutor.nexora.modules.automation.config.AutomationConfig
import com.robotutor.nexora.modules.feed.controllers.view.FeedView
import com.robotutor.nexora.modules.feed.models.FeedId
import com.robotutor.nexora.shared.adapters.outbound.cache.services.CacheService
import com.robotutor.nexora.shared.adapters.outbound.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FeedGateway(
    private val webClient: WebClientWrapper,
    private val automationConfig: AutomationConfig,
    private val cacheService: CacheService
) {

    fun getFeedByFeedId(feedId: FeedId): Mono<FeedView> {
        return cacheService.retrieve(FeedView::class.java, "FEED:$feedId") {
            webClient.get(
                baseUrl = automationConfig.feedServiceBaseUrl,
                path = automationConfig.feedByIdPath,
                uriVariables = mapOf("feedId" to feedId),
                returnType = FeedView::class.java
            )
        }
    }

}