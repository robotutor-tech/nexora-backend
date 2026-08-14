package com.robotutor.nexora.module.identity.infrastructure.persistence.mapper

import com.robotutor.nexora.module.identity.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.identity.domain.vo.Credential
import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.module.identity.domain.vo.HashedCredentialSecret
import com.robotutor.nexora.shared.domain.vo.principal.SubjectId
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.AccountDocument
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.CredentialDocument
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object AccountDocumentMapper : DocumentMapper<AccountAggregate, AccountDocument> {
    override fun toMongoDocument(domain: AccountAggregate): AccountDocument {
        return AccountDocument(
            id = domain.getObjectId(),
            accountId = domain.accountId.value,
            type = domain.type,
            principalId = domain.subjectId.value,
            createdBy = domain.createdBy?.value,
            credentials = domain.getCredentials().map {
                CredentialDocument(
                    kind = it.kind,
                    credentialId = it.credentialId.value,
                    secret = it.secret.value,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    metadata = it.metadata
                )
            },
            status = domain.getStatus(),
            createdAt = domain.createdAt,
            updatedAt = domain.getUpdatedAt(),
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: AccountDocument): AccountAggregate {
        return AccountAggregate.create(
            accountId = AccountId(document.accountId),
            type = document.type,
            subjectId = SubjectId(document.principalId),
            createdBy = document.createdBy?.let { ActorId(it) },
            credentials = document.credentials.map {
                Credential(
                    kind = it.kind,
                    credentialId = CredentialId(it.credentialId),
                    secret = HashedCredentialSecret(it.secret),
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt,
                    metadata = it.metadata
                )
            },
            status = document.status,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}
