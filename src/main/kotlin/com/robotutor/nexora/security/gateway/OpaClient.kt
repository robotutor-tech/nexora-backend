package com.robotutor.nexora.security.gateway

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.redis.services.CacheService
import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.filters.PolicyInput
import com.robotutor.nexora.webClient.WebClientWrapper
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