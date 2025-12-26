package com.robotutor.nexora.context.device.domain.specification

import com.robotutor.nexora.context.device.domain.aggregate.FeedAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface FeedSpecification : Specification<FeedAggregate>