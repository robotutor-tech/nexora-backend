package com.robotutor.nexora.context.iam.domain.aggregate

import com.robotutor.nexora.context.iam.domain.event.AccountCreatedEvent
import com.robotutor.nexora.context.iam.domain.event.CredentialUpdatedEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.vo.*
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId
import java.time.Instant

class AccountAggregate private constructor(
    val accountId: AccountId,
    val type: AccountType,
    val principalId: PrincipalId,
    val createdBy: ActorId?,
    val createdAt: Instant,
    private val credentials: MutableList<Credential>,
    private var status: AccountStatus,
    private var updatedAt: Instant,
) : AggregateRoot<AccountAggregate, AccountId, IAMEvent>(accountId) {

    fun getStatus(): AccountStatus = status
    fun getUpdatedAt(): Instant = updatedAt
    fun getCredentials(): List<Credential> = credentials.toList()

    companion object {
        fun register(
            accountId: AccountId,
            type: AccountType,
            principalId: PrincipalId,
            credentials: List<Credential>,
            createdBy: ActorId? = null,
        ): AccountAggregate {
            val account = create(accountId, type, principalId, createdBy, credentials)
            account.addEvent(AccountCreatedEvent(account.accountId, account.type, account.principalId))
            return account
        }

        fun create(
            accountId: AccountId,
            type: AccountType,
            principalId: PrincipalId,
            createdBy: ActorId? = null,
            credentials: List<Credential> = emptyList(),
            status: AccountStatus = AccountStatus.ACTIVE,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): AccountAggregate {
            return AccountAggregate(
                accountId = accountId,
                type = type,
                principalId = principalId,
                createdBy = createdBy,
                credentials = credentials.toMutableList(),
                status = status,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
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
    ACTIVE,
    DISABLED
}