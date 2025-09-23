package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.EntitlementFacade
import com.robotutor.nexora.modules.iam.interfaces.controller.EntitlementController
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.ResourceRequest
import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceContext
import com.robotutor.nexora.shared.domain.model.ResourceEntitlement
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class EntitlementApiFacade(private val entitlementController: EntitlementController) : EntitlementFacade {
    override fun authorize(requireAccess: RequireAccess, resourceId: String): Mono<Boolean> {
        return ContextDataResolver.getActorData()
            .flatMap { actorData ->
                val resourceRequest = ResourceRequest(
                    action = requireAccess.action,
                    resourceType = requireAccess.resource,
                    resourceId = resourceId
                )
                entitlementController.authorize(resourceRequest, actorData)
            }
    }

    override fun getEntitlements(requirePolicy: RequireAccess): Flux<ResourceEntitlement> {
        return ContextDataResolver.getActorData()
            .flatMapMany { actorData ->
                entitlementController.getEntitlements(requirePolicy.resource, requirePolicy.action, actorData)
            }
            .map { entitlement ->
                ResourceEntitlement(
                    resource = ResourceContext(
                        type = entitlement.resourceType,
                        id = entitlement.resourceId,
                        action = entitlement.action
                    ),
                    premisesId = PremisesId(entitlement.premisesId)
                )
            }
    }
}
