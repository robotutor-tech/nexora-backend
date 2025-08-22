package com.robotutor.nexora.common.security.adapters.client

import com.robotutor.nexora.common.security.application.ports.EntitlementFacade
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.modules.iam.interfaces.controller.EntitlementController
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class EntitlementClientFacade(private val entitlementController: EntitlementController) : EntitlementFacade {
    override fun getEntitlements(action: ActionType, resource: ResourceType): Flux<ResourceEntitlement> {
        return Mono.deferContextual {
            createMono(it.get(ActorData::class.java))
        }
            .flatMapMany {
                entitlementController.getEntitlements(resource, action, it)
            }
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