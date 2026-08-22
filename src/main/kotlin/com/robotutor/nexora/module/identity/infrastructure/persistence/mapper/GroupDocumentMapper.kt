package com.robotutor.nexora.module.identity.infrastructure.persistence.mapper

import com.robotutor.nexora.module.identity.domain.aggregate.Group
import com.robotutor.nexora.module.identity.domain.vo.GroupId
import com.robotutor.nexora.module.identity.domain.vo.RoleId
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.GroupDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object GroupDocumentMapper : DocumentMapper<Group, GroupDocument> {
    override fun toDocument(domain: Group): GroupDocument {
        return GroupDocument(
            id = domain.getObjectId(),
            groupId = domain.groupId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            type = domain.type,
            roleIds = domain.roleIds.map { it.value }.toSet(),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.getVersion(),
        )
    }

    override fun toDomain(document: GroupDocument): Group {
        return Group(
            groupId = GroupId(document.groupId),
            name = Name.of(document.name),
            premisesId = PremisesId(document.premisesId),
            roleIds = document.roleIds.map { RoleId(it) }.toSet(),
            type = document.type,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}
