package com.robotutor.nexora.common.messaging

import com.robotutor.nexora.common.messaging.mapper.EventMapper
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.common.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class ResourceCreatedEventPublisher(
    eventPublisher: KafkaEventPublisher, mapper: EventMapper<ResourceCreatedEvent>
) : EventPublisherImpl<ResourceCreatedEvent>(eventPublisher, mapper)
