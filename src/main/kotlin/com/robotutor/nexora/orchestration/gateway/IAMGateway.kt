package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.orchestration.config.IAMConfig
import com.robotutor.nexora.orchestration.gateway.view.PremisesActorView
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
class IAMGateway(private val webClient: WebClientWrapper, private val iamConfig: IAMConfig) {

    fun registerPremises(premisesId: PremisesId): Flux<PremisesActorView> {
        return webClient.postFlux(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.registerPremises,
            body = mapOf("premisesId" to premisesId),
            returnType = PremisesActorView::class.java
        )
    }

    fun getActors(): Flux<PremisesActorView> {
        return webClient.getFlux(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.actors,
            returnType = PremisesActorView::class.java
        )
    }
}