package com.robotutor.nexora.modules.device.domain.event

import com.robotutor.nexora.shared.domain.event.DomainEvent

sealed class DeviceEvent(name: String) : DomainEvent("device.$name")