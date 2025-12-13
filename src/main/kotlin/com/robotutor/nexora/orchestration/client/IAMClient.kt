package com.robotutor.nexora.orchestration.client

import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.orchestration.client.view.ActorResponse
import com.robotutor.nexora.orchestration.client.view.IAMAccountResponse
import com.robotutor.nexora.orchestration.client.view.UserResponse
import com.robotutor.nexora.orchestration.config.IamConfig
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class IAMClient(private val webClient: WebClientWrapper, private val iamConfig: IamConfig) {
    @Value("\${app.security.internal-access-token}")
    lateinit var internalAccessToken: String

    fun registerAccount(userResponse: UserResponse, password: String): Mono<IAMAccountResponse> {
        val payload = mapOf(
            "credentialId" to userResponse.email,
            "secret" to password,
            "kind" to "PASSWORD",
            "type" to "HUMAN"
        )
        return webClient.post(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.accountRegisterPath,
            body = payload,
            returnType = IAMAccountResponse::class.java,
            headers = mapOf("Authorization" to "Bearer $internalAccessToken")
        )
    }

    fun getActors(accountData: AccountData): Flux<ActorResponse> {
        val queryParams = LinkedMultiValueMap<String, String>()
        queryParams.add("accountId", accountData.accountId.value)
        return webClient.getFlux(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.getActorPath,
            returnType = ActorResponse::class.java,
            queryParams = queryParams
        )
    }
}