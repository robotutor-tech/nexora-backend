package com.robotutor.nexora.modules.widget.infrastructure.messaging

import com.robotutor.nexora.modules.widget.domain.event.WidgetEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class KafkaWidgetEventPublisher(
    eventPublisher: KafkaEventPublisher,
    mapper: EventMapper<WidgetEvent>
) : DomainEventPublisher<WidgetEvent>(eventPublisher, mapper)