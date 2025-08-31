package com.robotutor.nexora.modules.iam.interfaces.messaging.mapper

import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.infrastructure.messaging.message.ResourceCreatedEventMessage

object ResourceCreatedEventMapper {
    fun toResourceCreatedEvent(event: ResourceCreatedEventMessage): ResourceCreatedEvent {
        return ResourceCreatedEvent(event.resourceType, ResourceId(event.resourceId))
    }

}