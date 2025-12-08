package com.robotutor.nexora.context.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.context.iam.domain.vo.GroupId
import com.robotutor.nexora.context.iam.domain.vo.RoleId
import com.robotutor.nexora.context.iam.infrastructure.persistence.document.ActorDocument
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object ActorDocumentMapper : DocumentMapper<ActorAggregate, ActorDocument> {
    override fun toMongoDocument(domain: ActorAggregate): ActorDocument {
        return ActorDocument(
            actorId = domain.actorId.value,
            premisesId = domain.premisesId.value,
            accountId = domain.accountId.value,
            roleIds = domain.roleIds.map { it.value }.toSet(),
            groupIds = domain.groupIds.map { it.value }.toSet(),
            overrides = domain.overrides,
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: ActorDocument): ActorAggregate {
        return ActorAggregate(
            actorId = ActorId(document.actorId),
            accountId = AccountId(document.accountId),
            premisesId = PremisesId(document.premisesId),
            roleIds = document.roleIds.map { RoleId(it) }.toSet(),
            groupIds = document.groupIds.map { GroupId(it) }.toSet(),
            overrides = document.overrides,
            status = document.status,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
            version = document.version
        )
    }
}