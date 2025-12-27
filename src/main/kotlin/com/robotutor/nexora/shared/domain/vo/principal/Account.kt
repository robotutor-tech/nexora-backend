package com.robotutor.nexora.shared.domain.vo.principal

import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ValueObject

sealed class Account(
    open val accountId: AccountId,
    open val type: AccountType,
    open val principalId: PrincipalId
) : PrincipalData, ValueObject()