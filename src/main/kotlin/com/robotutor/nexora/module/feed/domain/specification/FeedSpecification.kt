package com.robotutor.nexora.module.feed.domain.specification

import com.robotutor.nexora.module.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface FeedSpecification : Specification<FeedAggregate>

