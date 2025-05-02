package com.robotutor.nexora.security.gateway

import com.robotutor.nexora.auth.models.InvitationId
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.exceptions.NexoraError
import com.robotutor.nexora.security.gateway.view.AuthenticationResponseData
import com.robotutor.nexora.security.gateway.view.InvitationResponseData
import com.robotutor.nexora.security.getTraceId
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.IAuthenticationData
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.models.TokenIdentifier
import com.robotutor.nexora.webClient.WebClientWrapper
import com.robotutor.nexora.webClient.exceptions.UnAuthorizedException
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import kotlin.collections.mapOf

@Component("SecurityAuthGateway")
class AuthGateway(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig,
    private val iamGateway: IAMGateway
) {
    private val logger = Logger(this::class.java)

    fun validate(exchange: ServerWebExchange): Mono<IAuthenticationData> {
        val traceId = getTraceId(exchange)
        return webClient.get(
            baseUrl = appConfig.authServiceBaseUrl,
            path = appConfig.validatePath,
            returnType = AuthenticationResponseData::class.java,
        )
            .mapNotNull {
                val identifier = it.tokenIdentifier
                when (identifier.type) {
                    TokenIdentifier.PREMISES_ACTOR -> iamGateway.getPremisesActor(identifier.id)
                    TokenIdentifier.AUTH_USER -> createMono(AuthUserData.from(it))
                    TokenIdentifier.INVITATION -> getInvitation(identifier.id)
                }
            }
            .flatMap { it }
            .onErrorResume { createMonoError(UnAuthorizedException(NexoraError.NEXORA0101)) }
            .logOnSuccess(logger, "Successfully authenticated user for $traceId")
            .logOnError(logger, "", "Failed to authenticate user for $traceId")
    }

    private fun getInvitation(invitationId: InvitationId): Mono<InvitationData> {
        return webClient.get(
            baseUrl = appConfig.authServiceBaseUrl,
            path = appConfig.invitationDevicesPath,
            returnType = InvitationResponseData::class.java,
            uriVariables = mapOf("invitationId" to invitationId),
            headers = mapOf(AUTHORIZATION to appConfig.internalAccessToken)
        )
            .map { InvitationData.from(it) }
    }
}