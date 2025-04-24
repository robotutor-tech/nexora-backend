package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.orchestration.config.IAMConfig
import com.robotutor.nexora.orchestration.config.InternalAccessTokenConfig
import com.robotutor.nexora.orchestration.config.PremisesConfig
import com.robotutor.nexora.orchestration.config.UserConfig
import com.robotutor.nexora.orchestration.gateway.view.PremisesView
import com.robotutor.nexora.orchestration.gateway.view.UserView
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IAMGateway(private val webClient: WebClientWrapper, private val iamConfig: IAMConfig) {

    fun registerPremises(premisesId: PremisesId): Mono<PremisesId> {
        return webClient.post(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.registerPremises,
            body = mapOf("premisesId" to premisesId),
            returnType = PremisesId::class.java
        )
    }
}