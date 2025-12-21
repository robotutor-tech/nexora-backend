package com.robotutor.nexora.context.iam.domain.specification

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.vo.PremisesId

class ActorByPremisesIdSpecification(val premisesId: PremisesId) : ActorSpecification {
    override fun isSatisfiedBy(candidate: ActorAggregate): Boolean {
        return candidate.premisesId == premisesId
    }
}