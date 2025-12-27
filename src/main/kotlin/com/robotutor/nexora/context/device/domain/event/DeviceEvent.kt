package com.robotutor.nexora.context.device.domain.event

import com.robotutor.nexora.context.device.domain.aggregate.DeviceMetadata
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId

sealed interface DeviceEvent : Event

data class DeviceRegisteredEvent(val deviceId: DeviceId, val name: Name, val premisesId: PremisesId) : DeviceEvent
data class DeviceActivatedEvent(val deviceId: DeviceId, val premisesId: PremisesId) : DeviceEvent
data class DeviceRegistrationFailedEvent(val accountId: AccountId) : DeviceEvent
data class DeviceCommissionedEvent(val deviceId: DeviceId, val actorId: ActorId, val premisesId: PremisesId) :
    DeviceEvent

data class DeviceMetadataUpdatedEvent(val deviceId: DeviceId, val metadata: DeviceMetadata) : DeviceEvent
data class DeviceRegistrationCompensatedEvent(val deviceId: DeviceId) : DeviceEvent
