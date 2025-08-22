package com.robotutor.nexora.modules.iam.domain.repository

import com.robotutor.nexora.modules.iam.domain.model.Entitlement
import com.robotutor.nexora.shared.domain.model.ActionType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.RoleId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface EntitlementRepository {
    fun save(entitlement: Entitlement): Mono<Entitlement>
    fun findAllByPremisesIdAndRoleIdAndResourceTypeAndAction(
        premisesId: PremisesId,
        roleId: RoleId,
        resourceType: ResourceType,
        action: ActionType
    ): Flux<Entitlement>
}