package com.robotutor.nexora.shared.infrastructure.messaging.mapper

import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.infrastructure.messaging.message.ResourceCreatedEventMessage
import org.springframework.stereotype.Service

@Service
class ResourceCreatedEventMapper : EventMapper<ResourceCreatedEvent> {
    override fun toEventMessage(event: ResourceCreatedEvent): ResourceCreatedEventMessage {
        return ResourceCreatedEventMessage(
            resourceId = event.resourceId.value,
            resourceType = event.resourceType
        )
    }
}

