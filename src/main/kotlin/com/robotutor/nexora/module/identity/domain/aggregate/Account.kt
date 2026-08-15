package com.robotutor.nexora.module.identity.domain.aggregate

import com.robotutor.nexora.module.identity.domain.event.AccountCreatedEvent
import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.vo.Credential
import com.robotutor.nexora.module.identity.domain.vo.HashedSecret
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.SubjectId
import com.robotutor.nexora.shared.domain.vo.AccountType
import java.time.Instant

class Account private constructor(
    val accountId: AccountId,
    val accountType: AccountType,
    val subjectId: SubjectId,
    val createdBy: ActorId?,
    val createdAt: Instant,
    val credential: Credential,
    private var status: AccountStatus,
    private var updatedAt: Instant,
) : AggregateRoot<Account, AccountId, IdentityEvent>(accountId) {

    fun getStatus(): AccountStatus = status
    fun getUpdatedAt(): Instant = updatedAt

    companion object {
        fun register(
            accountId: AccountId,
            type: AccountType,
            subjectId: SubjectId,
            credential: Credential,
            createdBy: ActorId? = null,
        ): Account {
            val account = create(accountId, type, subjectId, credential, createdBy)
            account.addEvent(AccountCreatedEvent(account.accountId, account.accountType, account.subjectId))
            return account
        }

        fun create(
            accountId: AccountId,
            type: AccountType,
            subjectId: SubjectId,
            credential: Credential,
            createdBy: ActorId? = null,
            status: AccountStatus = AccountStatus.ACTIVE,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): Account {
            return Account(
                accountId = accountId,
                accountType = type,
                subjectId = subjectId,
                createdBy = createdBy,
                credential = credential,
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun rotateCredential(hashedCredentialSecret: HashedSecret): Account {
//        val credential = getCredential(kind).rotate(hashedCredentialSecret)
//        credentials.removeIf { it.credentialId == credential.credentialId && it.kind == credential.kind }
//        credentials.add(credential)
//        updatedAt = Instant.now()
//        addEvent(CredentialUpdatedEvent(accountId, credential.kind))
        return this
    }

//    fun getCredential(credentialId: CredentialId): Credential {
//        return credentials.find { it.credentialId == credentialId }
//            ?: throw BadDataException(IdentityError.NEXORA0202)
//    }
//
//    fun getCredential(kind: CredentialType): Credential {
//        return credentials.find { it.kind == kind } ?: throw BadDataException(IdentityError.NEXORA0202)
//    }
//
//    fun ensureActive() {
//        if (status != AccountStatus.ACTIVE) {
//            throw BadDataException(IdentityError.NEXORA0204)
//        }
//    }
}

enum class AccountStatus {
    ACTIVE,
    DISABLED
}
