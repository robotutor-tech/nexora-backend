package com.robotutor.nexora.module.zone.domain.specification

import com.robotutor.nexora.module.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface ZoneSpecification : Specification<ZoneAggregate>