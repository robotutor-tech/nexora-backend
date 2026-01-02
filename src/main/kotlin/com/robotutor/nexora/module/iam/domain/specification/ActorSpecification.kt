package com.robotutor.nexora.module.iam.domain.specification

import com.robotutor.nexora.module.iam.domain.aggregate.ActorAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface ActorSpecification : Specification<ActorAggregate>
