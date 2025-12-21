package com.robotutor.nexora.orchestration.client

import com.robotutor.nexora.orchestration.client.view.AccountPayload
import com.robotutor.nexora.orchestration.client.view.ActorResponse
import com.robotutor.nexora.orchestration.client.view.DeviceResponse
import com.robotutor.nexora.orchestration.client.view.IAMAccountResponse
import com.robotutor.nexora.orchestration.config.IamConfig
import com.robotutor.nexora.shared.domain.vo.AccountData
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

    fun registerAccount(payload: AccountPayload): Mono<IAMAccountResponse> {
        val headers = mutableMapOf<String, String>()
        if (payload.type == "HUMAN") headers["Authorization"] = "Bearer $internalAccessToken"
        val path =
            if (payload.type == "HUMAN") iamConfig.registerHumanAccountPath else iamConfig.registerMachineAccountPath
        return webClient.post(
            baseUrl = iamConfig.baseUrl,
            path = path,
            body = payload,
            returnType = IAMAccountResponse::class.java,
            headers = headers
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

    fun registerActorForOwner(premisesId: String): Mono<ActorResponse> {
        return webClient.post(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.premisesOwnerRegisterPath,
            body = mapOf("premisesId" to premisesId),
            returnType = ActorResponse::class.java
        )
    }

    fun registerActorForDevice(device: DeviceResponse): Mono<ActorResponse> {
        return webClient.post(
            baseUrl = iamConfig.baseUrl,
            path = iamConfig.machineActor,
            body = mapOf("premisesId" to device.premisesId, "deviceId" to device.deviceId),
            returnType = ActorResponse::class.java
        )
    }
}