package com.robotutor.nexora.context.zone.domain.specification

import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.shared.domain.vo.PremisesId

class ZoneByPremisesSpecification(val premisesId: PremisesId) : ZoneSpecification {
    override fun isSatisfiedBy(candidate: ZoneAggregate): Boolean {
        return candidate.premisesId == premisesId
    }
}