package com.robotutor.nexora.common.security.adapters.client

import com.robotutor.nexora.common.security.application.ports.EntitlementFacade
import com.robotutor.nexora.modules.iam.interfaces.controller.EntitlementController
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceContext
import com.robotutor.nexora.shared.domain.model.ResourceEntitlement
import com.robotutor.nexora.shared.domain.model.ResourceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class EntitlementClientFacade(private val entitlementController: EntitlementController) : EntitlementFacade {
    override fun getEntitlements(action: ActionType, resourceType: ResourceType): Flux<ResourceEntitlement> {
        return ContextDataResolver.getActorData()
            .flatMapMany { actorData -> entitlementController.getEntitlements(resourceType, action, actorData) }
            .map { entitlementResponse ->
                ResourceEntitlement(
                    resource = ResourceContext(
                        type = entitlementResponse.resourceType,
                        id = entitlementResponse.resourceId,
                        action = entitlementResponse.action
                    ),
                    premisesId = entitlementResponse.premisesId
                )
            }
    }
}