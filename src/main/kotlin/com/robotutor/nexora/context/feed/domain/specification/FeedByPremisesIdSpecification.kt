package com.robotutor.nexora.context.feed.domain.specification

import com.robotutor.nexora.context.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.shared.domain.vo.PremisesId

class FeedByPremisesIdSpecification(val premisesId: PremisesId) : FeedSpecification {
    override fun isSatisfiedBy(candidate: FeedAggregate): Boolean {
        return candidate.premisesId == premisesId
    }
}

