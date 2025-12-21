package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.AccountActivatedEvent
import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.CredentialUpdatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.vo.Credential
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import com.robotutor.nexora.context.iam.domain.vo.HashedCredentialSecret
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.domain.vo.ActorId
import java.time.Instant

class AccountAggregate private constructor(
    val accountId: AccountId,
    val type: AccountType,
    private val credentials: MutableList<Credential>,
    val createdBy: ActorId?,
    val createdAt: Instant,
    private var status: AccountStatus,
    private var updatedAt: Instant,
) : AggregateRoot<AccountAggregate, AccountId, IAMDomainEvent>(accountId) {

    fun getStatus(): AccountStatus = status
    fun getUpdatedAt(): Instant = updatedAt
    fun getCredentials(): List<Credential> = credentials.toList()

    companion object {
        fun register(
            accountId: AccountId,
            type: AccountType,
            credentials: List<Credential>,
            createdBy: ActorId? = null,
        ): AccountAggregate {
            val accountAggregate = create(accountId, type, createdBy, credentials)
            accountAggregate.addEvent(AccountCreatedEvent(accountAggregate.accountId))
            return accountAggregate
        }

        fun create(
            accountId: AccountId,
            type: AccountType,
            createdBy: ActorId? = null,
            credentials: List<Credential> = emptyList(),
            status: AccountStatus = AccountStatus.REGISTERED,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): AccountAggregate {
            return AccountAggregate(
                accountId = accountId,
                type = type,
                createdBy = createdBy,
                credentials = credentials.toMutableList(),
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun activate(): AccountAggregate {
        if (status != AccountStatus.REGISTERED) {
            throw InvalidStateException(IAMError.NEXORA0207)
        }
        this.status = AccountStatus.ACTIVE
        this.updatedAt = Instant.now()
        addEvent(AccountActivatedEvent(accountId))
        return this
    }

    fun rotateCredential(hashedCredentialSecret: HashedCredentialSecret, kind: CredentialKind): AccountAggregate {
        val credential = getCredential(kind).rotate(hashedCredentialSecret)
        credentials.removeIf { it.credentialId == credential.credentialId && it.kind == credential.kind }
        credentials.add(credential)
        updatedAt = Instant.now()
        addEvent(CredentialUpdatedEvent(accountId, credential.kind))
        return this
    }

    fun getCredential(credentialId: CredentialId): Credential {
        return credentials.find { it.credentialId == credentialId }
            ?: throw BadDataException(IAMError.NEXORA0202)
    }

    fun getCredential(kind: CredentialKind): Credential {
        return credentials.find { it.kind == kind } ?: throw BadDataException(IAMError.NEXORA0202)
    }
}

enum class AccountStatus {
    REGISTERED,
    ACTIVE,
    DISABLED
}