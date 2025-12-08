package com.robotutor.nexora.common.security.domain.vo

import com.robotutor.nexora.context.iam.domain.aggregate.AccountType
import com.robotutor.nexora.shared.domain.vo.AccountId

sealed interface PrincipalData
data class AccountData(val accountId: AccountId, val type: AccountType) :
    PrincipalData

data class ActorData(val actorId: String, val roleId: String) : PrincipalData
data class InternalData(val id: String) : PrincipalData
//data class InvitationData(val invitationId: String) : Data