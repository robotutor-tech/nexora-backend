package com.robotutor.nexora.common.security.adapters.client

import com.robotutor.nexora.common.security.application.ports.OpaFacade
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.domain.model.PolicyInput
import com.robotutor.nexora.shared.adapters.webclient.WebClientWrapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OpaClientFacade(private val webClient: WebClientWrapper, private val appConfig: AppConfig) : OpaFacade {
    override fun evaluate(input: PolicyInput): Mono<Boolean> {
        return webClient.post(
            baseUrl = appConfig.opaBaseUrl,
            path = appConfig.opaPath,
            body = mapOf("input" to input),
            returnType = OpaResponse::class.java
        )
            .map { it.result }
    }
}

data class OpaResponse(val result: Boolean)
