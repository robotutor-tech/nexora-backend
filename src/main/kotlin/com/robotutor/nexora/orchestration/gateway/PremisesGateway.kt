package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.orchestration.config.PremisesConfig
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PremisesGateway(private val webClient: WebClientWrapper, private val premisesConfig: PremisesConfig) {

    fun registerPremises(name: String): Mono<PremisesView> {
        return webClient.post(
            baseUrl = premisesConfig.baseUrl,
            path = premisesConfig.register,
            body = mapOf("name" to name),
            returnType = PremisesView::class.java
        )
    }
}