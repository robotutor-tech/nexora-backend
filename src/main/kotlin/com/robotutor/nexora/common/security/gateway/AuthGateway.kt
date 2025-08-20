package com.robotutor.nexora.common.security.gateway

import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.models.AuthUserData
import com.robotutor.nexora.common.security.models.IAuthenticationData
import com.robotutor.nexora.common.security.models.InvitationData
import com.robotutor.nexora.modules.auth.interfaces.controller.dto.DeviceInvitationView
import com.robotutor.nexora.modules.auth.models.InvitationId
import com.robotutor.nexora.shared.adapters.cache.services.CacheService
import com.robotutor.nexora.shared.adapters.webclient.WebClientWrapper
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component("SecurityAuthGateway")
class AuthGateway(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig,
    private val iamGateway: IAMGateway,
    private val cacheService: CacheService
) {
    private val logger = Logger(this::class.java)

    fun validate(): Mono<IAuthenticationData> {
//        return cacheService.retrieve(AuthValidationView::class.java) {
//            webClient.get(
//                baseUrl = appConfig.authServiceBaseUrl,
//                path = appConfig.validatePath,
//                returnType = AuthValidationView::class.java,
//            )
//        }
//            .flatMap {
//                val identifier = it.identifier
//                when (identifier.type) {
//                    TokenIdentifier.PREMISES_ACTOR -> iamGateway.getPremisesActor(identifier.id, it.roleId!!)
//                    TokenIdentifier.AUTH_USER -> createMono(AuthUserData.from(it))
//                    TokenIdentifier.INVITATION -> getInvitation(identifier.id)
//                }
//            }
        return Mono.just(AuthUserData("userid") as IAuthenticationData)
            .logOnSuccess(logger, "Successfully authenticated user")
            .logOnError(logger, "", "Failed to authenticate user")
    }

    private fun getInvitation(invitationId: InvitationId): Mono<InvitationData> {
        return cacheService.retrieve(InvitationData::class.java) {
            webClient.get(
                baseUrl = appConfig.authServiceBaseUrl,
                path = appConfig.invitationDevicesPath,
                returnType = DeviceInvitationView::class.java,
                uriVariables = mapOf("invitationId" to invitationId),
                headers = mapOf(AUTHORIZATION to appConfig.internalAccessToken)
            )
                .map { InvitationData.from(it) }
        }
    }
}