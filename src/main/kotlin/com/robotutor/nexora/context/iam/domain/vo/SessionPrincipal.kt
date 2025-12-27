package com.robotutor.nexora.context.iam.domain.vo

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.shared.domain.vo.*
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
) :
    SessionPrincipal

data class ActorPrincipal(
    val actorId: ActorId,
    val premisesId: PremisesId,
    override val accountId: AccountId,
    override val type: AccountType,
    override val principalId: PrincipalId
) : SessionPrincipal
