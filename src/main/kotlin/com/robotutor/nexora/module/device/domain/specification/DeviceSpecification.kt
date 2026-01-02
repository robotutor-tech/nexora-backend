package com.robotutor.nexora.module.device.domain.specification

import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.specification.Specification

sealed interface DeviceSpecification : Specification<DeviceAggregate>