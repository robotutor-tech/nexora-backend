package com.robotutor.nexora.module.identity.infrastructure.persistence.document

import com.robotutor.nexora.module.identity.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.identity.domain.aggregate.AccountStatus
import com.robotutor.nexora.module.identity.domain.vo.CredentialKind
import com.robotutor.nexora.shared.domain.vo.principal.SubjectType
import com.robotutor.nexora.shared.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val ACCOUNT_COLLECTION = "accounts"

@TypeAlias("Account")
@Document(ACCOUNT_COLLECTION)
data class AccountDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val accountId: String,
    val type: SubjectType,
    val principalId: String,
    val createdBy: String?,
    val credentials: List<CredentialDocument>,
    val status: AccountStatus,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<AccountAggregate>

data class CredentialDocument(
    val kind: CredentialKind,
    val credentialId: String,
    val secret: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val metadata: Map<String, String>
)
