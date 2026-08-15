package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.shared.domain.vo.SubjectType

class AccountBySubjectTypeSpecification(val subjectType: SubjectType) : AccountSpecification {
    override fun isSatisfiedBy(candidate: Account): Boolean {
        return candidate.subjectType == subjectType
    }
}
