package com.robotutor.nexora.context.zone.infrastructure.messaging

import com.robotutor.nexora.context.zone.domain.event.ZoneDomainEvent
import com.robotutor.nexora.context.zone.infrastructure.messaging.mapper.ZoneDomainEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class ZoneDomainEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<ZoneDomainEvent>(eventPublisher, ZoneDomainEventMapper)