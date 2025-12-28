package com.robotutor.nexora.shared.domain.vo.principal

import com.robotutor.nexora.shared.domain.vo.AccountId

data class AccountData(
    override val accountId: AccountId,
    override val type: AccountType,
    override val principalId: PrincipalId
) : Account(accountId, type, principalId) {
}

enum class AccountType { HUMAN, MACHINE }