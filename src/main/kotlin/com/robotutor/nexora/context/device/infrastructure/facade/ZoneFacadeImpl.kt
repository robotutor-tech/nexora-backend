package com.robotutor.nexora.context.device.infrastructure.facade

import com.robotutor.nexora.context.device.application.facade.ZoneFacade
import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.infrastructure.config.ZoneConfig
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ZoneFacadeImpl(private val webClient: WebClientWrapper, private val zoneConfig: ZoneConfig, config: ZoneConfig) :
    ZoneFacade {

    override fun registerWidgets(device: DeviceAggregate, feedIds: List<FeedId>): Mono<Any> {
        return webClient.post(
            baseUrl = zoneConfig.baseUrl,
            path = zoneConfig.widgetsPath,
            body = mapOf(
                "feedIds" to feedIds.map { it.value },
                "zoneId" to device.zoneId.value,
                "modelNo" to device.getMetadata()!!.modelNo.value
            ),
            returnType = Any::class.java
        )
    }
}