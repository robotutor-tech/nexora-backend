package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.shared.domain.vo.AccountId

class ActorByAccountIdSpecification(val accountId: AccountId) : ActorSpecification {
    override fun isSatisfiedBy(candidate: Actor): Boolean {
        return candidate.accountId == accountId
    }
}
