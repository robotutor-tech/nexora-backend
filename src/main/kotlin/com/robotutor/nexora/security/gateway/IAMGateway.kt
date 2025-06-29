package com.robotutor.nexora.security.gateway

import com.robotutor.nexora.iam.controllers.view.ActorView
import com.robotutor.nexora.iam.controllers.view.EntitlementView
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.redis.services.CacheService
import com.robotutor.nexora.security.config.AppConfig
import com.robotutor.nexora.security.filters.ResourceContext
import com.robotutor.nexora.security.filters.ResourceEntitlement
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.WebClientWrapper
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Mono

@Component("SecurityPremisesGateway")
class IAMGateway(
    private val webClient: WebClientWrapper,
    private val appConfig: AppConfig,
    private val cacheService: CacheService
) {
    fun getPremisesActor(actorId: ActorId, roleId: RoleId): Mono<PremisesActorData> {
        return cacheService.retrieve(PremisesActorData::class.java, "Actor:$actorId:$roleId") {
            webClient.get(
                baseUrl = appConfig.iamServiceBaseUrl,
                path = appConfig.actorPath,
                returnType = ActorView::class.java,
                uriVariables = mapOf("actorId" to actorId, "roleId" to roleId),
                headers = mapOf(AUTHORIZATION to appConfig.internalAccessToken)
            )
                .map { PremisesActorData.from(it) }
        }
    }

    fun getEntitlements(action: ActionType, resource: ResourceType): Mono<List<ResourceEntitlement>> {
        return cacheService.retrieves(ResourceEntitlement::class.java, "Entitlements:$action:$resource") {
            webClient.getFlux(
                baseUrl = appConfig.iamServiceBaseUrl,
                path = appConfig.entitlementPath,
                returnType = EntitlementView::class.java,
                queryParams = LinkedMultiValueMap(
                    mapOf(
                        "resourceType" to listOf(resource.toString()),
                        "action" to listOf(action.toString())
                    )
                )
            )
                .map {
                    ResourceEntitlement(
                        resource = ResourceContext(it.resourceType, it.resourceId, it.action),
                        premisesId = it.premisesId
                    )
                }
        }
            .collectList()
    }
}