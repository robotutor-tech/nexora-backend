package com.robotutor.nexora.orchestration.client

import com.robotutor.nexora.orchestration.client.view.PremisesResponse
import com.robotutor.nexora.orchestration.config.PremisesConfig
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Flux

@Component
class PremisesClient(
    private val webClient: WebClientWrapper,
    private val premisesConfig: PremisesConfig,
) {
    fun getPremises(premisesIds: List<String>): Flux<PremisesResponse> {
        val queryParams = LinkedMultiValueMap<String, String>()
        premisesIds.forEach { premisesId -> queryParams.add("premisesIds", premisesId) }
        return webClient.getFlux(
            baseUrl = premisesConfig.baseUrl,
            path = premisesConfig.getPremisesPath,
            returnType = PremisesResponse::class.java,
            queryParams = queryParams
        )
    }
}