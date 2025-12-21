package com.robotutor.nexora.context.iam.domain.specification

import com.robotutor.nexora.context.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface ActorSpecification : Specification<ActorAggregate>
