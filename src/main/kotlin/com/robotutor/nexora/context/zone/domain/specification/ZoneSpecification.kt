package com.robotutor.nexora.context.zone.domain.specification

import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface ZoneSpecification : Specification<ZoneAggregate>