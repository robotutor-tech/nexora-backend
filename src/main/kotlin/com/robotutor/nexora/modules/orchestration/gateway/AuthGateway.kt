package com.robotutor.nexora.modules.orchestration.gateway

import com.robotutor.nexora.modules.orchestration.config.AuthConfig
import com.robotutor.nexora.modules.orchestration.config.InternalAccessTokenConfig
import com.robotutor.nexora.shared.adapters.webclient.WebClientWrapper
import org.springframework.stereotype.Component

@Component("OrchestrationAuthGateway")
class AuthGateway(
    private val webClient: WebClientWrapper,
    private val authConfig: AuthConfig,
    private val internalAccessTokenConfig: InternalAccessTokenConfig
) {

//
//    fun createDeviceActorToken(actor: PremisesActorData): Mono<TokenView> {
//        return webClient.post(
//            baseUrl = authConfig.baseUrl,
//            path = authConfig.deviceToken,
//            body = mapOf("actorId" to actor.actorId, "roleId" to actor.role.roleId),
//            returnType = TokenView::class.java,
//        )
//    }
}