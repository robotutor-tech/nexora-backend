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

    fun createWidget(widget: Widget, feed: FeedView, zoneId: ZoneId, tokenView: TokenView): Mono<WidgetView> {
        val body = mapOf("name" to widget.name, "type" to widget.type, "feed" to feed.feedId, "zoneId" to zoneId)

        return webClient.post(
            baseUrl = widgetConfig.baseUrl,
            path = widgetConfig.widgets,
            body = body,
            returnType = WidgetView::class.java,
            headers = mapOf(AUTHORIZATION to tokenView.token)
        )
    }
}