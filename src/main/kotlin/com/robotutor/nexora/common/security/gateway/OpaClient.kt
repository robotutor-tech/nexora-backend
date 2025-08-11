package com.robotutor.nexora.common.security.gateway

import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.shared.adapters.outbound.cache.services.CacheService
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.filters.PolicyInput
import com.robotutor.nexora.shared.adapters.outbound.webclient.WebClientWrapper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OpaClient(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig,
    private val cacheService: CacheService
) {
    private val logger = Logger(this::class.java)

    fun evaluate(policyInput: PolicyInput): Mono<Boolean> {
        return cacheService.retrieve(PolicyResult::class.java, key = policyInput.hashCode().toString()) {
            webClient.post(
                baseUrl = appConfig.opaBaseUrl,
                path = appConfig.opaPath,
                returnType = PolicyResult::class.java,
                body = mapOf("input" to policyInput),
            )
        }
            .map { it.result }
            .logOnSuccess(logger, "Successfully authenticated user")
            .logOnError(logger, "", "Failed to authenticate user")
    }

}

data class PolicyResult(val result: Boolean)