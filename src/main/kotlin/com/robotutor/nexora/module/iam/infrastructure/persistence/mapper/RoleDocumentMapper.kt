package com.robotutor.nexora.module.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.module.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.module.iam.domain.vo.Permission
import com.robotutor.nexora.module.iam.domain.vo.RoleId
import com.robotutor.nexora.module.iam.infrastructure.persistence.document.PermissionDocument
import com.robotutor.nexora.module.iam.infrastructure.persistence.document.RoleDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.common.persistence.mapper.DocumentMapper

object RoleDocumentMapper : DocumentMapper<RoleAggregate, RoleDocument> {
    override fun toMongoDocument(domain: RoleAggregate): RoleDocument {
        return RoleDocument(
            id = domain.getObjectId(),
            roleId = domain.roleId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            type = domain.type,
            permissions = domain.permissions.map {
                PermissionDocument(resourceType = it.resourceType, action = it.action, resource = it.resourceId.value)
            }.toSet(),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: RoleDocument): RoleAggregate {
        return RoleAggregate(
            roleId = RoleId(document.roleId),
            name = Name(document.name),
            premisesId = PremisesId(document.premisesId),
            permissions = document.permissions.map {
                Permission(
                    action = it.action,
                    resourceType = it.resourceType,
                    resourceId = ResourceId(it.resource),
                    premisesId = PremisesId(document.premisesId)
                )
            }.toSet(),
            type = document.type,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}