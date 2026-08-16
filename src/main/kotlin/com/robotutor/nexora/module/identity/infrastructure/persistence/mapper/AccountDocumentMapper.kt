package com.robotutor.nexora.module.identity.infrastructure.persistence.mapper

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.vo.Credential
import com.robotutor.nexora.module.identity.domain.vo.CredentialId
import com.robotutor.nexora.module.identity.domain.vo.HashedSecret
import com.robotutor.nexora.shared.domain.vo.SubjectId
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.AccountDocument
import com.robotutor.nexora.module.identity.infrastructure.persistence.document.CredentialDocument
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.persistence.mapper.DocumentMapper

object AccountDocumentMapper : DocumentMapper<Account, AccountDocument> {
    override fun toMongoDocument(domain: Account): AccountDocument {
        return AccountDocument(
            id = domain.getObjectId(),
            accountId = domain.accountId.value,
            accountType = domain.accountType,
            subjectId = domain.subjectId.value,
            createdBy = domain.createdBy?.value,
            credential = CredentialDocument(
                credentialId = domain.credential.credentialId.value,
                secret = domain.credential.hashedSecret.value,
            ),
            status = domain.getStatus(),
            createdAt = domain.createdAt,
            updatedAt = domain.getUpdatedAt(),
            version = domain.getVersion(),
        )
    }

    override fun toDomainModel(document: AccountDocument): Account {
        return Account.create(
            accountId = AccountId(document.accountId),
            type = document.accountType,
            subjectId = SubjectId.from(document.accountType, document.subjectId),
            createdBy = document.createdBy?.let { ActorId(it) },
            credential = Credential(
                credentialId = CredentialId.from(document.accountType, document.credential.credentialId),
                hashedSecret = HashedSecret.from(document.accountType, document.credential.secret),
            ),
            status = document.status,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
        ).setObjectIdAndVersion(document.id, document.version)
    }
}
