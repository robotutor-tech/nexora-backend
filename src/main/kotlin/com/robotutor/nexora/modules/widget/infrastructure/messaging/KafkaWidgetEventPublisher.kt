package com.robotutor.nexora.modules.widget.infrastructure.messaging

import com.robotutor.nexora.modules.widget.domain.event.WidgetEvent
import com.robotutor.nexora.modules.widget.infrastructure.messaging.mapper.WidgetEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class KafkaWidgetEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<WidgetEvent>(eventPublisher, WidgetEventMapper)