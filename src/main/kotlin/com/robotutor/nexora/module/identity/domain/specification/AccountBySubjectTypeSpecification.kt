package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.shared.domain.vo.AccountType

class AccountBySubjectTypeSpecification(val accountType: AccountType) : AccountSpecification {
    override fun isSatisfiedBy(candidate: Account): Boolean {
        return candidate.accountType == accountType
    }
}
