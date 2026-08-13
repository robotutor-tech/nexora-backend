package com.robotutor.nexora.module.zone.infrastructure.messaging

import com.robotutor.nexora.module.zone.domain.event.ZoneEvent
import com.robotutor.nexora.module.zone.domain.event.ZoneEventPublisher
import com.robotutor.nexora.module.zone.infrastructure.messaging.mapper.ZoneEventMapper
import com.robotutor.nexora.shared.message.EventPublisherImpl
import com.robotutor.nexora.shared.message.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class ZoneEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<ZoneEvent>(eventPublisher, ZoneEventMapper), ZoneEventPublisher
