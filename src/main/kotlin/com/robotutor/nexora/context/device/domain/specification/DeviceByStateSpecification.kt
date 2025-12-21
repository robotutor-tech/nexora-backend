package com.robotutor.nexora.context.device.domain.specification

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.aggregate.DeviceState

class DeviceByStateSpecification(val state: DeviceState) : DeviceSpecification {
    override fun isSatisfiedBy(candidate: DeviceAggregate): Boolean {
        return candidate.getState() == state
    }
}