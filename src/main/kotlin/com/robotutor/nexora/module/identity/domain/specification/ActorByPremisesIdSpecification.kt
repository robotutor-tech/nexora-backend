package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.vo.PremisesId

class ActorByPremisesIdSpecification(val premisesId: PremisesId) : ActorSpecification {
    override fun isSatisfiedBy(candidate: ActorAggregate): Boolean {
        return candidate.premisesId == premisesId
    }
}
