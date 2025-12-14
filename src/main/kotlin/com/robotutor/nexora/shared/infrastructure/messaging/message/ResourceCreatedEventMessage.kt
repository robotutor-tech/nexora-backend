package com.robotutor.nexora.shared.infrastructure.messaging.message

import com.robotutor.nexora.shared.domain.vo.ResourceType

data class ResourceCreatedEventMessage(val resourceId: String, val resourceType: ResourceType) :
    EventMessage("resource.created")