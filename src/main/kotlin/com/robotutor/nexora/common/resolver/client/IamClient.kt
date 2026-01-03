package com.robotutor.nexora.common.resolver.client

import com.robotutor.nexora.common.resource.annotation.ResourceSelector
import com.robotutor.nexora.common.webclient.WebClientWrapper
import com.robotutor.nexora.common.security.client.view.AuthorizeResponse
import com.robotutor.nexora.common.security.client.view.ResourceResponse
import com.robotutor.nexora.common.security.config.AppConfig
import com.robotutor.nexora.shared.application.annotation.Authorize
import com.robotutor.nexora.shared.domain.Entity
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.Resources
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("IamClientResourceResolver")
class IamClient(
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

    fun getResource(resourceSelector: ResourceSelector): Mono<Resources> {
        return webClient.post(
            baseUrl = appConfig.iamBaseUrl,
            path = appConfig.resourcePath,
            body = mapOf(
                "actionType" to resourceSelector.action,
                "resourceType" to resourceSelector.resourceType
            ),
            returnType = ResourceResponse::class.java
        )
            .map {
                val identifier = resourceSelector.resourceType.identifier
                Resources(
                    premisesId = PremisesId(it.premisesId),
                    resourceType = it.resourceType,
                    actionType = it.actionType,
                    resourceSelector = it.resourceSelector,
                    allowedIds = getIdentifiers(it.allowedIds, identifier),
                    deniedIds = getIdentifiers(it.deniedIds, identifier)
                )
            }
    }

    private fun getIdentifiers(ids: Set<String>, identifierClass: Class<out Identifier>): Set<Identifier> {
        return ids
            .map { id ->
                identifierClass
                    .getDeclaredConstructor(String::class.java)
                    .newInstance(id)
            }
            .toSet()
    }
}
