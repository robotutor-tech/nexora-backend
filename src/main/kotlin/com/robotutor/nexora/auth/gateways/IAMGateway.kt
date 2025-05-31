package com.robotutor.nexora.auth.gateways

import com.robotutor.nexora.auth.config.IAMConfig
import com.robotutor.nexora.auth.gateways.view.ActorView
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Mono

@Component("AuthIamGateway")
class IAMGateway(private val webClient: WebClientWrapper, private val iamConfig: IAMConfig) {
    @Value("\${app.orchestration.internal-access-token}")
    private lateinit var internalAccessToken: String


    fun getActor(actorId: String, roleId: String): Mono<ActorView> {
        return webClient.get(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.actorPath,
            uriVariables = mapOf("actorId" to actorId),
            queryParams = LinkedMultiValueMap(mapOf("roleId" to listOf(roleId))),
            returnType = ActorView::class.java,
            headers = mapOf(AUTHORIZATION to internalAccessToken)
        )
    }
}