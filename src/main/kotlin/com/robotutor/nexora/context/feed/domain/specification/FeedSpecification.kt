package com.robotutor.nexora.context.feed.domain.specification

import com.robotutor.nexora.context.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface FeedSpecification : Specification<FeedAggregate>

