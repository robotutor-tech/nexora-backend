package com.robotutor.nexora.module.identity.domain.aggregate

import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class Group(
    val groupId: GroupId,
    val name: Name,
    val premisesId: PremisesId,
    val roleIds: Set<RoleId>,
    val type: GroupType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) : AggregateRoot<Group, GroupId, IdentityEvent>(groupId) {
    companion object {
        fun register(name: Name, premisesId: PremisesId, type: GroupType, roleIds: List<RoleId>): Group {
            val roleAggregate = Group(
                groupId = GroupId.generate(),
                name = name,
                type = type,
                premisesId = premisesId,
                roleIds = roleIds.toSet(),
            )
            return roleAggregate
        }
    }
}

enum class GroupType {
    OWNER,
    ADMIN,
    USER,
    GUEST,
    CUSTOM,
}
