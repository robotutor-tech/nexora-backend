package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.aggregate.RoleAggregate
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.RoleDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object RoleDocumentMapper : DocumentMapper<RoleAggregate, RoleDocument> {
    override fun toMongoDocument(domain: RoleAggregate): RoleDocument {
        return RoleDocument(
            id = domain.getObjectId(),
            roleId = domain.roleId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            type = domain.type,
            permissions = domain.permissions,
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
            permissions = document.permissions,
            type = document.type,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}