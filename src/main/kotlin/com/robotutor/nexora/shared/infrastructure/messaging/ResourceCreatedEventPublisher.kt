package com.robotutor.nexora.shared.infrastructure.messaging

import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class ResourceCreatedEventPublisher(
    eventPublisher: KafkaEventPublisher, mapper: EventMapper<ResourceCreatedEvent>
) : EventPublisherImpl<ResourceCreatedEvent>(eventPublisher, mapper)
