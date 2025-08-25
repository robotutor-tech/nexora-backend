package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.ResourceEntitlement
import com.robotutor.nexora.shared.domain.model.ResourceType
import reactor.core.publisher.Flux

interface EntitlementFacade {
    fun getEntitlements(action: ActionType, resourceType: ResourceType): Flux<ResourceEntitlement>
}