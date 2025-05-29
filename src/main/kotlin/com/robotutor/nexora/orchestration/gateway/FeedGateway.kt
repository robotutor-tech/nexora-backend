package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.orchestration.config.FeedConfig
import com.robotutor.nexora.orchestration.gateway.view.FeedView
import com.robotutor.nexora.orchestration.models.Feed
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class FeedGateway(
    private val webClient: WebClientWrapper,
    private val feedConfig: FeedConfig,
) {

    fun createFeed(feed: List<Feed>, tokenView: TokenView): Mono<List<FeedView>> {
        val body = feed.map { mapOf("name" to it.name, "type" to it.type) }

        return webClient.postFlux(
            baseUrl = feedConfig.baseUrl,
            path = feedConfig.feedsBatch,
            body = body,
            returnType = FeedView::class.java,
            headers = mapOf(AUTHORIZATION to tokenView.token)
        )
            .collectList()
    }
}