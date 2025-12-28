package com.robotutor.nexora.context.device.infrastructure.facade

import com.robotutor.nexora.context.device.application.facade.FeedFacade
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.device.infrastructure.config.FeedConfig
import com.robotutor.nexora.context.device.infrastructure.facade.view.FeedResponse
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.common.http.client.WebClientWrapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class FeedApiFacade(
    private val webClient: WebClientWrapper,
    private val feedConfig: FeedConfig,
) : FeedFacade {
    override fun registerFeeds(
        deviceId: DeviceId,
        modelNo: ModelNo
    ): Flux<FeedId> {
        return webClient.postFlux(
            baseUrl = feedConfig.baseUrl,
            path = feedConfig.registerPath,
            body = mapOf("deviceId" to deviceId.value, "modelNo" to modelNo.value),
            returnType = FeedResponse::class.java,
        )
            .map { FeedId(it.feedId) }
    }
}