package com.robotutor.nexora.security.gateway

import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.gateway.view.AuthenticationResponseData
import com.robotutor.nexora.security.gateway.view.IdentifierType
import com.robotutor.nexora.security.getTraceId
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.IAuthenticationData
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component("SecurityAuthGateway")
class AuthGateway(private val webClient: WebClientWrapper, private val appConfig: AppConfig) {
    val logger = Logger(this::class.java)

    fun validate(exchange: ServerWebExchange): Mono<IAuthenticationData> {
        val traceId = getTraceId(exchange)
        return webClient.get(
            baseUrl = appConfig.authServiceBaseUrl,
            path = appConfig.validatePath,
            returnType = AuthenticationResponseData::class.java,
        )
            .mapNotNull {
                when (it.identifierType) {
                    IdentifierType.PREMISES_ACTOR -> PremisesActorData.from(it)
                    IdentifierType.AUTH_USER -> AuthUserData.from(it)
                    IdentifierType.INVITATION -> InvitationData.from(it)
                }
            }
            .logOnSuccess(logger, "Successfully authenticated user for $traceId")
            .logOnError(logger, "", "Failed to authenticate user for $traceId")
    }
}