package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.ResourceType
import java.time.Instant
import java.util.*

open class DomainEvent(name: String) {
    val id: String = UUID.randomUUID().toString()
    val occurredOn: Instant = Instant.now()
    val eventName: Name = Name(name)
}

data class ResourceCreatedEvent(
    val resourceType: ResourceType,
    val resourceId: ResourceId,
) : DomainEvent("shared.resource.created")
