package com.robotutor.nexora.modules.iam.infrastructure.facade

import com.robotutor.nexora.modules.iam.application.facade.OpaFacade
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.modules.iam.domain.entity.PolicyInput
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OpaApiFacade(private val webClient: WebClientWrapper, private val appConfig: AppConfig) : OpaFacade {
    override fun evaluate(input: PolicyInput): Mono<Boolean> {
        val policyInputRequest = mapOf(
            "input" to mapOf(
                "resource" to mapOf(
                    "type" to input.resource.type,
                    "id" to input.resource.id,
                    "action" to input.resource.action
                ),
                "premisesId" to input.premisesId.value,
                "entitlements" to input.entitlements
                    .map { entitlement ->
                        mapOf(
                            "resource" to mapOf(
                                "type" to entitlement.resource.type,
                                "id" to entitlement.resource.id,
                                "action" to entitlement.resource.action
                            ),
                            "premisesId" to entitlement.premisesId.value
                        )
                    }
            ))
        return webClient.post(
            baseUrl = appConfig.opaBaseUrl,
            path = appConfig.opaPath,
            body = policyInputRequest,
            returnType = OpaResponse::class.java
        )
            .map { it.result }
    }
}

data class OpaResponse(val result: Boolean)
