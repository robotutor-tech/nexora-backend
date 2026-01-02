package com.robotutor.nexora.module.zone.infrastructure.messaging

import com.robotutor.nexora.module.zone.domain.event.ZoneEvent
import com.robotutor.nexora.module.zone.domain.event.ZoneEventPublisher
import com.robotutor.nexora.module.zone.infrastructure.messaging.mapper.ZoneEventMapper
import com.robotutor.nexora.common.messaging.EventPublisherImpl
import com.robotutor.nexora.common.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class ZoneEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<ZoneEvent>(eventPublisher, ZoneEventMapper), ZoneEventPublisher
