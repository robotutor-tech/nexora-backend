package com.robotutor.nexora.security.gateway

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.gateway.view.AuthenticationResponseData
import com.robotutor.nexora.security.getTraceId
import com.robotutor.nexora.security.models.UserData
import com.robotutor.nexora.security.models.UserPremisesData
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component("SecurityAuthGateway")
class AuthGateway(private val webClient: WebClientWrapper, private val appConfig: AppConfig) {
    val logger = Logger(this::class.java)

    fun validate(exchange: ServerWebExchange, full: Boolean): Mono<Pair<UserData, UserPremisesData?>> {
        val traceId = getTraceId(exchange)
        val queryParams = LinkedMultiValueMap(mapOf("full" to listOf(full.toString())))
        return webClient.get(
            baseUrl = appConfig.authServiceBaseUrl,
            path = appConfig.validatePath,
            returnType = AuthenticationResponseData::class.java,
            queryParams = queryParams
        )
            .map { data -> Pair(UserData.from(data), UserPremisesData.from(data)) }
            .logOnSuccess(logger, "Successfully authenticated user for $traceId")
            .logOnError(logger, "", "Failed to authenticate user for $traceId")
    }
}