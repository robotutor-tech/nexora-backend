package com.robotutor.nexora.modules.feed.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage

data class DeviceFeedsCreatedMessage(val deviceId: String, val feedIds: List<String>) : EventMessage