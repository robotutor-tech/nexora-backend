package com.robotutor.nexora.modules.device.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.ModelNo
import com.robotutor.nexora.shared.domain.model.ZoneId

data class DeviceCreatedEvent(
    val deviceId: DeviceId,
    val modelNo: ModelNo,
    val zoneId: ZoneId
) : DomainEvent("device.device.created")
