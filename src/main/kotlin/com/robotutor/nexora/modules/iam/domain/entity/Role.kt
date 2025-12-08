package com.robotutor.nexora.modules.iam.domain.entity

import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.domain.model.RoleType
import java.time.Instant

data class Role(
    val roleId: RoleId,
    val premisesId: PremisesId,
    val name: Name,
    val roleType: RoleType,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val version: Long? = null
) : AggregateRoot<Role, RoleId, DomainEvent>(roleId) {
    companion object {
        fun create(roleId: RoleId, premisesId: PremisesId, name: Name, roleType: RoleType): Role {
            return Role(roleId = roleId, premisesId = premisesId, name = name, roleType = roleType)
        }
    }
}
