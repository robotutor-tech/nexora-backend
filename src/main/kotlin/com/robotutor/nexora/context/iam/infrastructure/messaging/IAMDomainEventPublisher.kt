package com.robotutor.nexora.context.iam.infrastructure.messaging

import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.infrastructure.messaging.mapper.IAMDomainEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class IAMDomainEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<IAMDomainEvent>(eventPublisher, IAMDomainEventMapper)