package com.robotutor.nexora.module.identity.domain.aggregate

import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.exception.IdentityError
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import com.robotutor.nexora.module.identity.domain.vo.PermissionOverride
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import java.time.Instant

data class Actor(
    val actorId: ActorId,
    val accountId: AccountId,
    val premisesId: PremisesId,
    val roleIds: Set<RoleId>,
    val groupIds: Set<GroupId>,
    val overrides: Set<PermissionOverride> = emptySet(),
    val status: ActorStatus = ActorStatus.ACTIVE,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
) : AggregateRoot<Actor, ActorId, IdentityEvent>(actorId) {
    fun ensureActive() {
        if (status != ActorStatus.ACTIVE) {
            throw InvalidStateException(IdentityError.NEXORA0206)
        }
    }

    companion object {
        fun register(
            accountId: AccountId,
            premisesId: PremisesId,
            roleIds: List<RoleId>,
            groupIds: List<GroupId>
        ): Actor {
            val actor = Actor(
                actorId = ActorId.generate(),
                accountId = accountId,
                premisesId = premisesId,
                roleIds = roleIds.toSet(),
                groupIds = groupIds.toSet()
            )
            return actor
        }
    }
}

enum class ActorStatus { ACTIVE, INACTIVE }
