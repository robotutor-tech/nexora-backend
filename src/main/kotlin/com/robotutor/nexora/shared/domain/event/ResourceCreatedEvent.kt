package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.DomainEvent

data class ResourceCreatedEvent(
    val resourceType: ResourceType,
    val resourceId: ResourceId,
) : DomainEvent

