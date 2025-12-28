package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.AccessAuthorizer
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.infrastructure.facade.view.AuthorizeResponse
import com.robotutor.nexora.common.security.interfaces.annotation.HttpAuthorize
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.common.http.infrastructure.client.WebClientWrapper
import com.robotutor.nexora.common.security.interfaces.view.AuthorizedResources
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class AccessAuthorizerClient(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig
) : AccessAuthorizer {
    override fun authorize(httpAuthorize: HttpAuthorize, resourceId: ResourceId): Mono<Boolean> {
        return webClient.post(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.authorizeResourcePath,
            body = mapOf(
                "resourceId" to resourceId.value,
                "actionType" to httpAuthorize.action,
                "resourceType" to httpAuthorize.resource
            ),
            returnType = AuthorizeResponse::class.java
        )
            .map { it.isAuthorized }
    }

    override fun getAuthorizedScope(exchange: ServerWebExchange, httpAuthorize: HttpAuthorize): Mono<AuthorizedResources> {
        return webClient.post(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.resourcePath,
            body = mapOf(
                "actionType" to httpAuthorize.action,
                "resourceType" to httpAuthorize.resource
            ),
            returnType = AuthorizedResources::class.java
        )
    }
}
