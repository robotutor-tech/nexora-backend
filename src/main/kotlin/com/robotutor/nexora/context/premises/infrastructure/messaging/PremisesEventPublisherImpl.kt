package com.robotutor.nexora.context.premises.infrastructure.messaging

import com.robotutor.nexora.context.premises.domain.event.PremisesEvent
import com.robotutor.nexora.context.premises.domain.event.PremisesEventPublisher
import com.robotutor.nexora.context.premises.infrastructure.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.common.messaging.EventPublisherImpl
import com.robotutor.nexora.common.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class PremisesEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<PremisesEvent>(eventPublisher, PremisesEventMapper), PremisesEventPublisher
