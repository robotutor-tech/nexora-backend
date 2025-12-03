package com.robotutor.nexora.orchestration.client

import com.robotutor.nexora.orchestration.client.view.IAMIdentityResponse
import com.robotutor.nexora.orchestration.client.view.UserResponse
import com.robotutor.nexora.orchestration.config.IamConfig
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class IAMClient(private val webClient: WebClientWrapper, private val iamConfig: IamConfig) {
    fun registerIdentity(userResponse: UserResponse, password: String): Mono<IAMIdentityResponse> {
        return webClient.post(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.path,
            body = mapOf("email" to userResponse.email, "userId" to userResponse.userId, "password" to password),
            returnType = IAMIdentityResponse::class.java
        )
    }
}