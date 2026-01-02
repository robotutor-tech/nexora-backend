package com.robotutor.nexora.module.device.domain.policy

import com.robotutor.nexora.module.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.module.device.domain.aggregate.DeviceState
import com.robotutor.nexora.shared.domain.policy.Policy
import com.robotutor.nexora.shared.domain.policy.PolicyResult
import org.springframework.stereotype.Service

@Service
class CommissionDevicePolicy : Policy<DeviceAggregate> {
    override fun evaluate(input: DeviceAggregate): PolicyResult {
        val reasons = mutableListOf<String>()
        if (input.getState() != DeviceState.REGISTERED) {
            reasons.add("Device is not in commission state")
        }
        return if (reasons.isEmpty()) PolicyResult.allow() else PolicyResult.deny(reasons)
    }
}