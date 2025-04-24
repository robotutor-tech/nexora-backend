package com.robotutor.nexora.orchestration.gateway

import com.robotutor.nexora.orchestration.config.InternalAccessTokenConfig
import com.robotutor.nexora.orchestration.config.UserConfig
import com.robotutor.nexora.orchestration.gateway.view.UserView
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserGateway(
    private val webClient: WebClientWrapper,
    private val userConfig: UserConfig,
    private val internalAccessTokenConfig: InternalAccessTokenConfig
) {

    fun registerUser(name: String, email: String): Mono<UserView> {
        return webClient.post(
            baseUrl = userConfig.baseUrl,
            path = userConfig.register,
            body = mapOf("name" to name, "email" to email),
            headers = mapOf("Authorization" to internalAccessTokenConfig.internalAccessToken),
            returnType = UserView::class.java
        )
    }
}