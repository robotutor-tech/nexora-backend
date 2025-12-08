package com.robotutor.nexora.modules.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.iam.domain.entity.Role
import com.robotutor.nexora.modules.iam.infrastructure.persistence.document.RoleDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object RoleDocumentMapper : DocumentMapper<Role, RoleDocument> {
    override fun toMongoDocument(domain: Role): RoleDocument = RoleDocument(
        id = null,
        roleId = domain.roleId.value,
        premisesId = domain.premisesId.value,
        name = domain.name.value,
        roleType = domain.roleType,
        createdAt = domain.createdAt,
        updatedAt = domain.updatedAt,
        version = domain.version
    )

    override fun toDomainModel(document: RoleDocument): Role = Role(
        roleId = RoleId(document.roleId),
        premisesId = PremisesId(document.premisesId),
        name = Name(document.name),
        roleType = document.roleType,
        createdAt = document.createdAt,
        updatedAt = document.updatedAt,
        version = document.version
    )
}
