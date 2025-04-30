package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.orchestration.config.PremisesConfig
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Flux
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

    fun getPremises(premisesIds: List<PremisesId>): Flux<PremisesView> {
        return webClient.getFlux(
            baseUrl = premisesConfig.baseUrl,
            path = premisesConfig.register,
            queryParams = LinkedMultiValueMap(mapOf("premisesIds" to premisesIds)),
            returnType = PremisesView::class.java
        )
    }
}