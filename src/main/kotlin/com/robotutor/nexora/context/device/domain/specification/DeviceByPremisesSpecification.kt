package com.robotutor.nexora.context.device.domain.specification

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.vo.PremisesId

class DeviceByPremisesSpecification(val premisesId: PremisesId) : DeviceSpecification {
    override fun isSatisfiedBy(candidate: DeviceAggregate): Boolean {
        return candidate.premisesId == premisesId
    }
}