package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.orchestration.config.AuthConfig
import com.robotutor.nexora.orchestration.config.InternalAccessTokenConfig
import com.robotutor.nexora.orchestration.gateway.view.InvitationView
import com.robotutor.nexora.security.models.UserId
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
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

    fun validateInvitation(modelNo: String): Mono<InvitationView> {
        return webClient.get(
            baseUrl = authConfig.baseUrl,
            path = authConfig.validateInvitation,
            returnType = InvitationView::class.java,
            queryParams = LinkedMultiValueMap(mapOf("modelNo" to listOf(modelNo)))
        )
    }
}