package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.vo.GroupId
import com.robotutor.nexora.context.iam.domain.vo.PermissionOverride
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class ActorAggregate(
    val actorId: ActorId,
    val accountId: AccountId,
    val premisesId: PremisesId,
    val roleIds: Set<RoleId>,
    val groupIds: Set<GroupId>,
    val overrides: Set<PermissionOverride> = emptySet(),
    val status: ActorStatus = ActorStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) : AggregateRoot<ActorAggregate, ActorId, IAMEvent>(actorId) {
    companion object {
        fun register(
            accountId: AccountId,
            premisesId: PremisesId,
            roleIds: List<RoleId>,
            groupIds: List<GroupId>
        ): ActorAggregate {
            val actorAggregate = ActorAggregate(
                actorId = ActorId.generate(),
                accountId = accountId,
                premisesId = premisesId,
                roleIds = roleIds.toSet(),
                groupIds = groupIds.toSet()
            )
            return actorAggregate
        }
    }
}

enum class ActorStatus { ACTIVE, INACTIVE }