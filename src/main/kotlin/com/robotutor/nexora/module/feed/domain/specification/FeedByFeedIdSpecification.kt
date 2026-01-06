package com.robotutor.nexora.module.feed.domain.specification

import com.robotutor.nexora.module.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.shared.domain.vo.FeedId

class FeedByFeedIdSpecification(val feedId: FeedId) : FeedSpecification {
    override fun isSatisfiedBy(candidate: FeedAggregate): Boolean {
        return candidate.feedId == feedId
    }
}

