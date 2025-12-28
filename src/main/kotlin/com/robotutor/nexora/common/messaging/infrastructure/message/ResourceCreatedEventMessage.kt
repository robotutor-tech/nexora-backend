package com.robotutor.nexora.common.messaging.infrastructure.message

import com.robotutor.nexora.shared.domain.vo.ResourceType

data class ResourceCreatedEventMessage(val resourceId: String, val resourceType: ResourceType) :
    EventMessage("resource.created")