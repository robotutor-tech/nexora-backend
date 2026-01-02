package com.robotutor.nexora.module.iam.domain.vo

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.module.iam.domain.aggregate.AccountAggregate
import com.robotutor.nexora.module.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.vo.*
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.AccountType
import com.robotutor.nexora.shared.domain.vo.principal.PrincipalId

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountPrincipal::class, name = "ACCOUNT"),
    JsonSubTypes.Type(value = ActorPrincipal::class, name = "ACTOR")
)
sealed interface SessionPrincipal {
    val accountId: AccountId
    val type: AccountType
    val principalId: PrincipalId
}

data class AccountPrincipal(
    override val accountId: AccountId,
    override val type: AccountType,
    override val principalId: PrincipalId
) : SessionPrincipal {
    companion object {
        fun from(account: AccountAggregate): AccountPrincipal {
            return AccountPrincipal(account.accountId, account.type, account.principalId)
        }
    }
}

data class ActorPrincipal(
    val actorId: ActorId,
    val premisesId: PremisesId,
    override val accountId: AccountId,
    override val type: AccountType,
    override val principalId: PrincipalId
) : SessionPrincipal {
    companion object {
        fun from(actor: ActorAggregate, account: AccountData): ActorPrincipal {
            return ActorPrincipal(
                actorId = actor.actorId,
                premisesId = actor.premisesId,
                accountId = actor.accountId,
                type = account.type,
                principalId = account.principalId
            )
        }
    }
}
