package com.robotutor.nexora.modules.iam.repositories

import com.robotutor.nexora.modules.iam.models.Entitlement
import com.robotutor.nexora.modules.iam.models.EntitlementId
import com.robotutor.nexora.modules.iam.models.EntitlementStatus
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.common.security.filters.annotations.ActionType
import com.robotutor.nexora.common.security.filters.annotations.ResourceType
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface EntitlementRepository : ReactiveCrudRepository<Entitlement, EntitlementId> {
    fun findAllByPremisesIdAndRoleIdAndResourceTypeAndActionAndStatus(
        premisesId: PremisesId,
        roleId: RoleId,
        resourceType: ResourceType,
        action: ActionType,
        status: EntitlementStatus = EntitlementStatus.ACTIVE
    ): Flux<Entitlement>
}
