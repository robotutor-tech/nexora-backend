package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.vo.GroupId
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class GroupAggregate(
    val groupId: GroupId,
    val name: Name,
    val premisesId: PremisesId,
    val roleIds: Set<RoleId>,
    val type: GroupType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) : AggregateRoot<GroupAggregate, GroupId, IAMEvent>(groupId) {
    companion object {
        fun register(name: Name, premisesId: PremisesId, type: GroupType, roleIds: List<RoleId>): GroupAggregate {
            val roleAggregate = GroupAggregate(
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