package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.orchestration.config.WidgetConfig
import com.robotutor.nexora.orchestration.gateway.view.FeedView
import com.robotutor.nexora.orchestration.models.Widget
import com.robotutor.nexora.webClient.WebClientWrapper
import com.robotutor.nexora.widget.controllers.view.WidgetView
import com.robotutor.nexora.zone.models.ZoneId
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class WidgetGateway(
    private val webClient: WebClientWrapper,
    private val widgetConfig: WidgetConfig,
) {

    fun createWidgets(
        widgets: List<Widget>,
        feeds: List<FeedView>,
        zoneId: ZoneId,
        tokenView: TokenView
    ): Mono<List<WidgetView>> {
        val data = widgets.map { widget ->
            val feed = feeds.find { widget.name == it.name }!!
            mapOf("name" to widget.name, "type" to widget.type, "feed" to feed.feedId, "zoneId" to zoneId)
        }

        return webClient.postFlux(
            baseUrl = widgetConfig.baseUrl,
            path = widgetConfig.widgetsBatch,
            body = data,
            returnType = WidgetView::class.java,
            headers = mapOf(AUTHORIZATION to tokenView.token)
        ).collectList()
    }
}