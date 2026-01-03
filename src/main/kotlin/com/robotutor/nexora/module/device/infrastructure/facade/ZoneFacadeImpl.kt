package com.robotutor.nexora.module.device.infrastructure.facade

import com.robotutor.nexora.module.device.application.facade.ZoneFacade
import com.robotutor.nexora.module.device.domain.vo.ModelNo
import com.robotutor.nexora.module.device.infrastructure.config.ZoneConfig
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import com.robotutor.nexora.common.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ZoneFacadeImpl(private val webClient: WebClientWrapper, private val zoneConfig: ZoneConfig) : ZoneFacade {

    override fun registerWidgets(zoneId: ZoneId, modelNo: ModelNo, feedIds: List<FeedId>): Mono<Any> {
        return webClient.post(
            baseUrl = zoneConfig.baseUrl,
            path = zoneConfig.widgetsPath,
            body = mapOf(
                "feedIds" to feedIds.map { it.value },
                "zoneId" to zoneId.value,
                "modelNo" to modelNo.value
            ),
            returnType = Any::class.java
        )
    }
}