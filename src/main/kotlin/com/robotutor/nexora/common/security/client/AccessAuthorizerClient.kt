package com.robotutor.nexora.common.security.client

import com.robotutor.nexora.common.security.client.view.AuthorizeResponse
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.common.webclient.WebClientWrapper
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.vo.ResourceId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AccessAuthorizerClient(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig
) {
    fun authorize(httpAuthorize: Authorize, resourceId: ResourceId): Mono<Boolean> {
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
}
