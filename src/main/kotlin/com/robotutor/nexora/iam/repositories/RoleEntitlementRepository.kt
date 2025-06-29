package com.robotutor.nexora.iam.repositories

import com.robotutor.nexora.iam.models.RoleEntitlement
import com.robotutor.nexora.iam.models.RoleEntitlementId
import com.robotutor.nexora.iam.models.RoleId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RoleEntitlementRepository : ReactiveCrudRepository<RoleEntitlement, RoleEntitlementId> {
    fun findAllByRoleId(roleId: RoleId): Flux<RoleEntitlement>
}
