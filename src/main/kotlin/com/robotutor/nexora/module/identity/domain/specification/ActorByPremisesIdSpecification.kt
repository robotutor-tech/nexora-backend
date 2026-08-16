package com.robotutor.nexora.module.identity.domain.specification

import com.robotutor.nexora.module.identity.domain.aggregate.Actor
import com.robotutor.nexora.shared.domain.vo.PremisesId

class ActorByPremisesIdSpecification(val premisesId: PremisesId) : ActorSpecification {
    override fun isSatisfiedBy(candidate: Actor): Boolean {
        return candidate.premisesId == premisesId
    }
}
