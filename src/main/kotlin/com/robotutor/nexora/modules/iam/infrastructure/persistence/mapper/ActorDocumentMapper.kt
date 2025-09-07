package com.robotutor.nexora.modules.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.iam.domain.entity.Actor
import com.robotutor.nexora.modules.iam.infrastructure.persistence.document.ActorDocument
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.PrincipalDocumentMapper

object ActorDocumentMapper : DocumentMapper<Actor, ActorDocument> {
    override fun toMongoDocument(domain: Actor): ActorDocument {
        return ActorDocument(
            actorId = domain.actorId.value,
            premisesId = domain.premisesId.value,
            roleIds = domain.roleIds.map { it.value },
            state = domain.state,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.version,
            principalType = domain.principalType,
            principal = PrincipalDocumentMapper.toActorPrincipalDocument(domain.principal)
        )
    }

    override fun toDomainModel(document: ActorDocument): Actor {
        return Actor(
            actorId = ActorId(document.actorId),
            premisesId = PremisesId(document.premisesId),
            roleIds = document.roleIds.map { RoleId(it) },
            state = document.state,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
            version = document.version,
            principalType = document.principalType,
            principal = PrincipalDocumentMapper.toActorPrincipalContext(document.principal)
        )
    }
}