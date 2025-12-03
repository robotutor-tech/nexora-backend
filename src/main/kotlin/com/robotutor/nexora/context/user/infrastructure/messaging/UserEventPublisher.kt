package com.robotutor.nexora.context.user.infrastructure.messaging

import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.mapper.UserEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class UserEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<UserEvent>(eventPublisher, UserEventMapper)