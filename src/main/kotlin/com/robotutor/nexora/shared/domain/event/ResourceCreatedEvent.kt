package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.vo.ResourceId
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.domain.Event

data class ResourceCreatedEvent(
    val resourceType: ResourceType,
    val resourceId: ResourceId,
) : Event

