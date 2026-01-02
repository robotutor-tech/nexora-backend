package com.robotutor.nexora.module.device.domain.specification

import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.vo.ActorId

class DeviceByRegisteredBySpecification(val actorId: ActorId) : DeviceSpecification {
    override fun isSatisfiedBy(candidate: DeviceAggregate): Boolean {
        return candidate.registeredBy == actorId
    }
}