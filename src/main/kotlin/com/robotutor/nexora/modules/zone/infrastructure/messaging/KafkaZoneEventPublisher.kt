package com.robotutor.nexora.modules.zone.infrastructure.messaging

import com.robotutor.nexora.modules.zone.domain.event.ZoneEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class KafkaZoneEventPublisher(
    eventPublisher: KafkaEventPublisher,
    mapper: EventMapper<ZoneEvent>
) : DomainEventPublisher<ZoneEvent>(eventPublisher, mapper)