package com.robotutor.nexora.shared.domain.vo.principal

import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.PremisesId

data class ActorData(
    val actorId: ActorId,
    val premisesId: PremisesId,
    override val accountId: AccountId,
    override val type: AccountType,
    override val principalId: PrincipalId
) : Account(accountId, type, principalId) {
    fun toAccountData(): AccountData {
        return AccountData(accountId, type, principalId)
    }
}