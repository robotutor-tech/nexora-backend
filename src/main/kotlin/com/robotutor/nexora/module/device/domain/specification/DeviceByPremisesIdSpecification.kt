package com.robotutor.nexora.module.device.domain.specification

import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.vo.PremisesId

class DeviceByPremisesIdSpecification(val premisesId: PremisesId) : DeviceSpecification {
    override fun isSatisfiedBy(candidate: DeviceAggregate): Boolean {
        return candidate.premisesId == premisesId
    }
}