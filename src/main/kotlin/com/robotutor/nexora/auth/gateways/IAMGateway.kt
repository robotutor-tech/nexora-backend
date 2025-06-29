package com.robotutor.nexora.auth.gateways

import com.robotutor.nexora.auth.config.IAMConfig
import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component("AuthIamGateway")
class IAMGateway(private val webClient: WebClientWrapper, private val iamConfig: IAMConfig) {

    fun getActor(actorId: String, roleId: String): Mono<ActorView> {
        return webClient.get(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.actorPath,
            uriVariables = mapOf("actorId" to actorId, "roleId" to roleId),
            returnType = ActorView::class.java,
        )
    }
}