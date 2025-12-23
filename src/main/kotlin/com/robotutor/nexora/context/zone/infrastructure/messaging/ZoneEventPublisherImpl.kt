package com.robotutor.nexora.context.zone.infrastructure.messaging

import com.robotutor.nexora.context.zone.domain.event.ZoneEvent
import com.robotutor.nexora.context.zone.domain.event.ZoneEventPublisher
import com.robotutor.nexora.context.zone.infrastructure.messaging.mapper.ZoneEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.EventPublisherImpl
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class ZoneEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<ZoneEvent>(eventPublisher, ZoneEventMapper), ZoneEventPublisher
