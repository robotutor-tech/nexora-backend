package com.robotutor.nexora.module.identity.domain.aggregate

import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.vo.Permission
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class Role(
    val roleId: RoleId,
    val name: Name,
    val premisesId: PremisesId,
    val permissions: Set<Permission>,
    val type: RoleType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) : AggregateRoot<Role, RoleId, IdentityEvent>(roleId) {
    companion object {
        fun register(name: Name, premisesId: PremisesId, type: RoleType, permissions: List<Permission>): Role {
            val role = Role(
                roleId = RoleId.generate(),
                name = name,
                type = type,
                premisesId = premisesId,
                permissions = permissions.toSet(),
            )
            return role
        }
    }
}

enum class RoleType {
    FULL_ACCESS,
    FULL_READ,
    FULL_WRITE,
    READ_ONLY,
    CONTROL_ONLY,
    DEVICE_ACCESS,
    CUSTOM,
}
