package com.robotutor.nexora.module.identity.infrastructure.persistence.mapper

import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import com.robotutor.nexora.module.identity.domain.vo.Permission
import com.robotutor.nexora.module.identity.domain.vo.PermissionOverride
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.ActorDocument
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.PermissionDocument
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.PermissionOverrideDocument
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object ActorDocumentMapper : DocumentMapper<Actor, ActorDocument> {
    override fun toDocument(domain: Actor): ActorDocument {
        return ActorDocument(
            id = domain.getObjectId(),
            actorId = domain.actorId.value,
            premisesId = domain.premisesId.value,
            accountId = domain.accountId.value,
            roleIds = domain.roleIds.map { it.value }.toSet(),
            groupIds = domain.groupIds.map { it.value }.toSet(),
            overrides = domain.overrides.map {
                PermissionOverrideDocument(
                    PermissionDocument(
                        resourceType = it.permission.resourceType,
                        action = it.permission.action,
                        resource = it.permission.resourceId.value
                    ),
                    effect = it.effect
                )
            }.toSet(),
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.getVersion()
        )
    }

    override fun toDomain(document: ActorDocument): Actor {
        return Actor(
            actorId = ActorId(document.actorId),
            accountId = AccountId(document.accountId),
            premisesId = PremisesId(document.premisesId),
            roleIds = document.roleIds.map { RoleId(it) }.toSet(),
            groupIds = document.groupIds.map { GroupId(it) }.toSet(),
            overrides = document.overrides.map {
                PermissionOverride(
                    Permission(
                        it.permission.action,
                        it.permission.resourceType,
                        ResourceId(it.permission.resource),
                        PremisesId(document.premisesId)
                    ),
                    it.effect
                )
            }.toSet(),
            status = document.status,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}
