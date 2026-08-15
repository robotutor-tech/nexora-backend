package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Account
import com.robotutor.nexora.module.identity.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.SubjectId

class AccountBySubjectIdSpecification(val subjectId: SubjectId) : AccountSpecification {
    override fun isSatisfiedBy(candidate: Account): Boolean {
        return candidate.subjectId == subjectId
    }
}
