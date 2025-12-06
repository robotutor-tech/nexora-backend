package com.robotutor.nexora.context.iam.domain.vo

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.shared.domain.vo.AccountId

sealed interface TokenPrincipalContext
data class AccountTokenPrincipalContext(
    val accountId: AccountId,
    val type: AccountType
) : TokenPrincipalContext

data class ActorTokenPrincipalContext(
    val actorId: String,
    val roleId: String,
) : TokenPrincipalContext