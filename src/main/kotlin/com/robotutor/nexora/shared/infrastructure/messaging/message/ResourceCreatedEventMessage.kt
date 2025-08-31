package com.robotutor.nexora.shared.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.event.EventMessage
import com.robotutor.nexora.shared.domain.model.ResourceType

data class ResourceCreatedEventMessage(val resourceId: String, val resourceType: ResourceType) : EventMessage