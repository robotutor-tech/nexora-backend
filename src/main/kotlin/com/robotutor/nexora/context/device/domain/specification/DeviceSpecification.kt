package com.robotutor.nexora.context.device.domain.specification

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface DeviceSpecification : Specification<DeviceAggregate>