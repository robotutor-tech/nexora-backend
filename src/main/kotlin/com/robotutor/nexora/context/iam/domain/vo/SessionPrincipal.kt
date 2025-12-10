package com.robotutor.nexora.context.iam.domain.vo

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.robotutor.nexora.common.security.domain.vo.AccountData
import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = AccountPrincipal::class, name = "ACCOUNT"),
    JsonSubTypes.Type(value = ActorPrincipal::class, name = "ACTOR")
)
sealed interface SessionPrincipal
data class AccountPrincipal(val accountId: AccountId, val accountType: AccountType) : SessionPrincipal

data class ActorPrincipal(
    val accountPrincipal: AccountPrincipal,
    val actorId: ActorId,
    val premisesId: PremisesId
) : SessionPrincipal {
    constructor(
        accountId: AccountId,
        accountType: AccountType,
        actorId: ActorId,
        premisesId: PremisesId
    ) : this(AccountPrincipal(accountId, accountType), actorId, premisesId)

    constructor(
        accountData: AccountData,
        actorId: ActorId,
        premisesId: PremisesId
    ) : this(AccountPrincipal(accountData.accountId, accountData.type), actorId, premisesId)
}

