package com.robotutor.nexora.context.user.infrastructure.messaging

import com.robotutor.nexora.context.user.domain.event.UserEvent
import com.robotutor.nexora.context.user.domain.event.UserEventPublisher
import com.robotutor.nexora.context.user.infrastructure.messaging.mapper.UserEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.EventPublisherImpl
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class UserEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<UserEvent>(eventPublisher, UserEventMapper), UserEventPublisher
