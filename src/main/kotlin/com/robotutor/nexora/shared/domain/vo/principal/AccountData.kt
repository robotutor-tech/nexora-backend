package com.robotutor.nexora.shared.domain.vo.principal

import com.robotutor.nexora.shared.domain.utility.validation
import com.robotutor.nexora.shared.domain.vo.AccountId

data class AccountData(
    override val accountId: AccountId,
    override val type: AccountType,
    override val principalId: PrincipalId
) : Account(accountId, type, principalId) {

    init {
        validate()
    }

    override fun validate() {
        validation(principalId.value.isNotBlank()) { "Principal id must not be blank" }
    }

    fun isHuman(): Boolean {
        return type == AccountType.HUMAN
    }

    fun isMachine(): Boolean {
        return type == AccountType.MACHINE
    }
}

enum class AccountType { HUMAN, MACHINE }