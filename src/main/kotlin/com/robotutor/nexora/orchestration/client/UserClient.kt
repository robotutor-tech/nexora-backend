package com.robotutor.nexora.orchestration.client

import com.robotutor.nexora.orchestration.client.view.UserResponse
import com.robotutor.nexora.orchestration.config.UserConfig
import com.robotutor.nexora.orchestration.controller.view.UserRegistrationRequest
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserClient(
    private val webClient: WebClientWrapper,
    private val userConfig: UserConfig,
) {
    @Value("\${app.security.internal-access-token}")
    lateinit var internalAccessToken: String

    fun registerUser(user: UserRegistrationRequest): Mono<UserResponse> {
        return webClient.post(
            baseUrl = userConfig.baseUrl,
            path = userConfig.path,
            body = mapOf("email" to user.email, "name" to user.name, "mobile" to user.mobile),
            returnType = UserResponse::class.java,
            headers = mapOf("Authorization" to "Bearer $internalAccessToken")
        )
    }
}