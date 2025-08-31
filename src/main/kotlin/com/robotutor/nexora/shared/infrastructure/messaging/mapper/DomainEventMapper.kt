package com.robotutor.nexora.shared.infrastructure.messaging.mapper


import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventMessage
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.infrastructure.messaging.message.ResourceCreatedEventMessage

open class DomainEventMapper(val mapper: EventMapper) : EventMapper {
    override fun toEventMessage(event: DomainEvent): EventMessage {
        return try {
            mapper.toEventMessage((event))
        } catch (e: IllegalArgumentException) {
            when (event) {
                is ResourceCreatedEvent -> toResourceCreatedEventMessage(event)
                else -> throw e
            }
        }
    }

    private fun toResourceCreatedEventMessage(event: ResourceCreatedEvent): ResourceCreatedEventMessage {
        return ResourceCreatedEventMessage(resourceId = event.resourceId.value, resourceType = event.resourceType)
    }
}