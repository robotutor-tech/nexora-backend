package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.AccessAuthorizer
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.security.infrastructure.facade.view.AuthorizeResponse
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.infrastructure.webclient.WebClientWrapper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccessAuthorizerClient(private val webClient: WebClientWrapper, private val appConfig: AppConfig) :
    AccessAuthorizer {
    override fun authorize(requireAccess: RequireAccess, resourceId: ResourceId): Mono<Boolean> {
        return webClient.post(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.authorizeResourcePath,
            body = mapOf(
                "resourceId" to resourceId.value,
                "actionType" to requireAccess.action,
                "resourceType" to requireAccess.resource
            ),
            returnType = AuthorizeResponse::class.java
        )
            .map { it.isAuthorized }
    }
}
