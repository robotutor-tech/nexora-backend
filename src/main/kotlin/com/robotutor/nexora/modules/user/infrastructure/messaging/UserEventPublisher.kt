package com.robotutor.nexora.modules.user.infrastructure.messaging

import com.robotutor.nexora.modules.user.domain.event.UserEvent
import com.robotutor.nexora.modules.user.infrastructure.messaging.mapper.UserEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class UserEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<UserEvent>(eventPublisher, UserEventMapper)