package com.robotutor.nexora.module.iam.domain.specification

import com.robotutor.nexora.module.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.vo.AccountId

class ActorByAccountIdSpecification(val accountId: AccountId) : ActorSpecification {
    override fun isSatisfiedBy(candidate: ActorAggregate): Boolean {
        return candidate.accountId == accountId
    }
}