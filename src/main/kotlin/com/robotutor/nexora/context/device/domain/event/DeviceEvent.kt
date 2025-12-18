package com.robotutor.nexora.context.device.domain.event

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.domain.BusinessEvent
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface DeviceEvent : Event

sealed interface DeviceDomainEvent : DomainEvent, DeviceEvent
data class DeviceRegisteredEvent(val deviceId: DeviceId, val name: Name, val premisesId: PremisesId) : DeviceDomainEvent
data class DeviceCommissionedEvent(val deviceId: DeviceId, val accountId: AccountId, val premisesId: PremisesId) :
    DeviceDomainEvent


sealed interface DeviceBusinessEvent : BusinessEvent, DeviceEvent
data class DeviceRegistrationCompensatedEvent(val deviceId: DeviceId) : DeviceBusinessEvent
