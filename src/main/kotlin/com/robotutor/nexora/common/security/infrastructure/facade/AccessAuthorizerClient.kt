package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.AccessAuthorizer
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.infrastructure.facade.view.AuthorizeResponse
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class AccessAuthorizerClient(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig
) : AccessAuthorizer {
    override fun authorize(authorize: Authorize, resourceId: ResourceId): Mono<Boolean> {
        return webClient.post(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.authorizeResourcePath,
            body = mapOf(
                "resourceId" to resourceId.value,
                "actionType" to authorize.action,
                "resourceType" to authorize.resource
            ),
            returnType = AuthorizeResponse::class.java
        )
            .map { it.isAuthorized }
    }

    override fun getAuthorizedScope(exchange: ServerWebExchange, authorize: Authorize): Mono<AuthorizedResources> {
        return webClient.post(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.resourcePath,
            body = mapOf(
                "actionType" to authorize.action,
                "resourceType" to authorize.resource
            ),
            returnType = AuthorizedResources::class.java
        )
    }
}
