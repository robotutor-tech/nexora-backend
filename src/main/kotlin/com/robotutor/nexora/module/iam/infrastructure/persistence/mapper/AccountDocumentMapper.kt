package com.robotutor.nexora.module.iam.infrastructure.persistence.mapper

import com.robotutor.nexora.module.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.iam.domain.vo.Credential
import com.robotutor.nexora.module.iam.domain.vo.CredentialId
import com.robotutor.nexora.module.iam.domain.vo.HashedCredentialSecret
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId
import com.robotutor.nexora.module.iam.infrastructure.persistence.document.AccountDocument
import com.robotutor.nexora.module.iam.infrastructure.persistence.document.CredentialDocument
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.common.persistence.mapper.DocumentMapper

object AccountDocumentMapper : DocumentMapper<AccountAggregate, AccountDocument> {
    override fun toMongoDocument(domain: AccountAggregate): AccountDocument {
        return AccountDocument(
            id = domain.getObjectId(),
            accountId = domain.accountId.value,
            type = domain.type,
            principalId = domain.principalId.value,
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
            principalId = PrincipalId(document.principalId),
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
