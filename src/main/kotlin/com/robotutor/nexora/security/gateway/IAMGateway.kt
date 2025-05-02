package com.robotutor.nexora.security.gateway

import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.gateway.view.ActorResponseData
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component("SecurityPremisesGateway")
class IAMGateway(private val webClient: WebClientWrapper, private val appConfig: AppConfig) {
    fun getPremisesActor(actorId: ActorId): Mono<PremisesActorData> {
        return webClient.get(
            baseUrl = appConfig.iamServiceBaseUrl,
            path = appConfig.actorPath,
            returnType = ActorResponseData::class.java,
            uriVariables = mapOf("actorId" to actorId),
            headers = mapOf(AUTHORIZATION to appConfig.internalAccessToken)
        )
            .map { PremisesActorData.from(it) }
    }
}