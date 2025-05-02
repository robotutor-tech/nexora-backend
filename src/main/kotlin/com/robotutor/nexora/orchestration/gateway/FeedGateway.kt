package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.orchestration.config.FeedConfig
import com.robotutor.nexora.orchestration.gateway.view.FeedView
import com.robotutor.nexora.orchestration.models.Feed
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FeedGateway(
    private val webClient: WebClientWrapper,
    private val feedConfig: FeedConfig,
) {

    fun createFeed(feed: Feed, tokenView: TokenView): Mono<FeedView> {
        val body = mapOf("name" to feed.name, "type" to feed.type)

        return webClient.post(
            baseUrl = feedConfig.baseUrl,
            path = feedConfig.feeds,
            body = body,
            returnType = FeedView::class.java,
            headers = mapOf(AUTHORIZATION to tokenView.token)
        )
    }
}