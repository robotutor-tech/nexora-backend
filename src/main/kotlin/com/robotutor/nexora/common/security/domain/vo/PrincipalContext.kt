package com.robotutor.nexora.common.security.domain.vo

import com.robotutor.nexora.shared.domain.vo.*

sealed interface PrincipalContext {
    fun toPrincipalData(): PrincipalData
}

data class AccountPrincipalContext(val accountId: AccountId, val type: AccountType) :
    PrincipalContext {
    override fun toPrincipalData(): PrincipalData {
        return AccountData(accountId, type)
    }
}

data class ActorPrincipalContext(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountId: AccountId,
    val type: AccountType
) : PrincipalContext {
    override fun toPrincipalData(): PrincipalData {
        return ActorData(actorId, premisesId, accountId, type)
    }
}

data class InternalPrincipalContext(val id: String) : PrincipalContext {
    override fun toPrincipalData(): PrincipalData {
        return InternalData(id)
    }
}
//data class InvitationPrincipalContext(val invitationId: String) : PrincipalContext