package com.robotutor.nexora.common.security.domain.vo

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.shared.domain.vo.AccountId

sealed interface PrincipalContext {
    fun toPrincipalData(): PrincipalData
}

data class AccountPrincipalContext(val accountId: AccountId, val type: AccountType) :
    PrincipalContext {
    override fun toPrincipalData(): PrincipalData {
        return AccountData(accountId, type)
    }
}

data class ActorPrincipalContext(val actorId: String, val roleId: String) : PrincipalContext {
    override fun toPrincipalData(): PrincipalData {
        return ActorData(actorId, roleId)
    }
}

data class InternalPrincipalContext(val id: String) : PrincipalContext {
    override fun toPrincipalData(): PrincipalData {
        return InternalData(id)
    }
}
//data class InvitationPrincipalContext(val invitationId: String) : PrincipalContext