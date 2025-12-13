package com.robotutor.nexora.context.user.infrastructure.messaging

import com.robotutor.nexora.context.user.domain.event.UserDomainEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.mapper.UserDomainEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class UserDomainEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<UserDomainEvent>(eventPublisher, UserDomainEventMapper)

