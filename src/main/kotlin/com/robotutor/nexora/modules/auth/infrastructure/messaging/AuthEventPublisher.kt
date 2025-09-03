package com.robotutor.nexora.modules.auth.infrastructure.messaging

import com.robotutor.nexora.modules.auth.domain.event.AuthEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class AuthEventPublisher(
    eventPublisher: KafkaEventPublisher,
    mapper: EventMapper<AuthEvent>
) : DomainEventPublisher<AuthEvent>(eventPublisher, mapper) {
}