package com.robotutor.nexora.modules.orchestration.gateway

import com.robotutor.nexora.modules.auth.controllers.views.TokenView
import com.robotutor.nexora.modules.orchestration.config.AuthConfig
import com.robotutor.nexora.modules.orchestration.config.InternalAccessTokenConfig
import com.robotutor.nexora.common.security.models.PremisesActorData
import com.robotutor.nexora.common.security.models.UserId
import com.robotutor.nexora.shared.adapters.outbound.webclient.WebClientWrapper
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

    fun createDeviceActorToken(actor: PremisesActorData): Mono<TokenView> {
        return webClient.post(
            baseUrl = authConfig.baseUrl,
            path = authConfig.deviceToken,
            body = mapOf("actorId" to actor.actorId, "roleId" to actor.role.roleId),
            returnType = TokenView::class.java,
        )
    }
}