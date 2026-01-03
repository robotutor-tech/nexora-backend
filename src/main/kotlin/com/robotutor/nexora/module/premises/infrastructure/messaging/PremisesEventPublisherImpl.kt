package com.robotutor.nexora.module.premises.infrastructure.messaging

import com.robotutor.nexora.module.premises.domain.event.PremisesEvent
import com.robotutor.nexora.module.premises.domain.event.PremisesEventPublisher
import com.robotutor.nexora.module.premises.infrastructure.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.common.message.EventPublisherImpl
import com.robotutor.nexora.common.message.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class PremisesEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<PremisesEvent>(eventPublisher, PremisesEventMapper), PremisesEventPublisher
