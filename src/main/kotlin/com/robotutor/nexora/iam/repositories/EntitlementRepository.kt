package com.robotutor.nexora.iam.repositories

import com.robotutor.nexora.iam.models.Entitlement
import com.robotutor.nexora.iam.models.EntitlementId
import com.robotutor.nexora.iam.models.EntitlementStatus
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.filters.annotations.ActionType
import com.robotutor.nexora.security.filters.annotations.ResourceType
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
