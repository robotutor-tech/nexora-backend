package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.device.controllers.view.DeviceView
import com.robotutor.nexora.orchestration.config.IAMConfig
import com.robotutor.nexora.orchestration.gateway.view.FeedView
import com.robotutor.nexora.orchestration.gateway.view.PolicyView
import com.robotutor.nexora.orchestration.gateway.view.PremisesActorView
import com.robotutor.nexora.orchestration.models.Policy
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
            path = iamConfig.actorsPath,
            returnType = PremisesActorView::class.java
        )
    }

    fun registerActorAsBot(device: DeviceView): Mono<PremisesActorView> {
        return webClient.post(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.registerDevice,
            body = mapOf("deviceId" to device.deviceId, "type" to device.type),
            returnType = PremisesActorView::class.java
        )
    }

    fun createPolicies(policies: List<Policy>, feeds: List<FeedView>, token: TokenView): Mono<List<PolicyView>> {
        val data = policies.map { policy ->
            val feed = feeds.find { it.name == policy.name }!!
            mapOf(
                "premisesId" to feed.premisesId,
                "name" to "${feed.feedId.replace("/ /g", "_")}_${policy.access}",
                "type" to "LOCAL",
                "feedId" to feed.feedId,
                "access" to policy.access
            )
        }
        return webClient.postFlux(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.policyBatchPath,
            body = data,
            headers = mapOf("Authorization" to token.token),
            returnType = PolicyView::class.java
        )
            .collectList()
    }
}