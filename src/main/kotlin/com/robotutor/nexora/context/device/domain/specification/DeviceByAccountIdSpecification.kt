package com.robotutor.nexora.context.device.domain.specification

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.vo.AccountId

class DeviceByAccountIdSpecification(val accountId: AccountId) : DeviceSpecification {
    override fun isSatisfiedBy(candidate: DeviceAggregate): Boolean {
        return candidate.accountId == accountId
    }
}