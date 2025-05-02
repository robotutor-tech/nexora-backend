package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.auth.controllers.views.TokenView
import com.robotutor.nexora.orchestration.config.AuthConfig
import com.robotutor.nexora.orchestration.config.InternalAccessTokenConfig
import com.robotutor.nexora.orchestration.gateway.view.PremisesActorView
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component("OrchestrationAuthGateway")
class AuthGateway(
    private val webClient: WebClientWrapper,
    private val authConfig: AuthConfig,
    private val internalAccessTokenConfig: InternalAccessTokenConfig
) {

    fun registerUser(userId: UserId, email: String, password: String): Mono<UserId> {
        return webClient.post(
            baseUrl = authConfig.baseUrl,
            path = authConfig.register,
            body = mapOf("userId" to userId, "email" to email, "password" to password),
            returnType = UserId::class.java,
            headers = mapOf("Authorization" to internalAccessTokenConfig.internalAccessToken)
        )
    }


    fun createDeviceActorToken(actor: PremisesActorView): Mono<TokenView> {
        return webClient.post(
            baseUrl = authConfig.baseUrl,
            path = authConfig.deviceToken,
            body = mapOf("actorId" to actor.actorId),
            returnType = TokenView::class.java,
        )
    }
}