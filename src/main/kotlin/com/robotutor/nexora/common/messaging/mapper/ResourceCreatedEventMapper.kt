package com.robotutor.nexora.common.messaging.mapper

import com.robotutor.nexora.common.messaging.message.ResourceCreatedEventMessage
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
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

