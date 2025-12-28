package com.robotutor.nexora.context.device.domain.specification

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.context.device.domain.vo.DeviceId

class DeviceByDeviceIdSpecification(val deviceId: DeviceId) : DeviceSpecification {
    override fun isSatisfiedBy(candidate: DeviceAggregate): Boolean {
        return candidate.deviceId == deviceId
    }
}