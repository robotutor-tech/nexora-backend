package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.application.annotation.RequireAccess
import com.robotutor.nexora.shared.domain.model.ResourceEntitlement
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface EntitlementFacade {
    fun authorize(requireAccess: RequireAccess, resourceId: String): Mono<Boolean>
    fun getEntitlements(requirePolicy: RequireAccess): Flux<ResourceEntitlement>
}