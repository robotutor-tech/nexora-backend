package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.vo.Permission
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class RoleAggregate(
    val roleId: RoleId,
    val name: Name,
    val premisesId: PremisesId,
    val permissions: Set<Permission>,
    val type: RoleType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) : AggregateRoot<RoleAggregate, RoleId, IAMDomainEvent>(roleId) {
    companion object {
        fun register(name: Name, premisesId: PremisesId, type: RoleType, permissions: List<Permission>): RoleAggregate {
            val roleAggregate = RoleAggregate(
                roleId = RoleId.generate(),
                name = name,
                type = type,
                premisesId = premisesId,
                permissions = permissions.toSet(),
            )
            return roleAggregate
        }
    }
}

enum class RoleType {
    FULL_ACCESS,
    FULL_READ,
    FULL_WRITE,
    READ_ONLY,
    CONTROL_ONLY,
    CUSTOM,
}