package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.aggregate.GroupAggregate
import com.robotutor.nexora.context.iam.domain.vo.GroupId
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.GroupDocument
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object GroupDocumentMapper : DocumentMapper<GroupAggregate, GroupDocument> {
    override fun toMongoDocument(domain: GroupAggregate): GroupDocument {
        return GroupDocument(
            groupId = domain.groupId.value,
            name = domain.name.value,
            premisesId = domain.premisesId.value,
            type = domain.type,
            roleIds = domain.roleIds.map { it.value }.toSet(),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.version,
        )
    }

    override fun toDomainModel(document: GroupDocument): GroupAggregate {
        return GroupAggregate(
            groupId = GroupId(document.groupId),
            name = Name(document.name),
            premisesId = PremisesId(document.premisesId),
            roleIds = document.roleIds.map { RoleId(it) }.toSet(),
            type = document.type,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
            version = document.version,
        )
    }
}