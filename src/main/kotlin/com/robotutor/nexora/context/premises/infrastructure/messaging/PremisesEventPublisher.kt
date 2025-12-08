package com.robotutor.nexora.context.premises.infrastructure.messaging

import com.robotutor.nexora.context.premises.domain.event.PremisesEvent
import com.robotutor.nexora.context.premises.infrastructure.messaging.mapper.PremisesEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class PremisesEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<PremisesEvent>(eventPublisher, PremisesEventMapper)