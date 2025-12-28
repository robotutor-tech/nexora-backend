package com.robotutor.nexora.common.messaging.infrastructure

import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.common.messaging.infrastructure.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class ResourceCreatedEventPublisher(
    eventPublisher: KafkaEventPublisher, mapper: EventMapper<ResourceCreatedEvent>
) : EventPublisherImpl<ResourceCreatedEvent>(eventPublisher, mapper)
