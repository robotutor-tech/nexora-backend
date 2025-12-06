package com.robotutor.nexora.context.iam.infrastructure.messaging

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.infrastructure.messaging.mapper.IAMEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class AuthEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<IAMEvent>(eventPublisher, IAMEventMapper)