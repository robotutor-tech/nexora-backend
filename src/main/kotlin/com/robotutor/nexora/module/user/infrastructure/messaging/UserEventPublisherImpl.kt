package com.robotutor.nexora.module.user.infrastructure.messaging

import com.robotutor.nexora.module.user.domain.event.UserEvent
import com.robotutor.nexora.module.user.domain.event.UserEventPublisher
import com.robotutor.nexora.module.user.infrastructure.messaging.mapper.UserEventMapper
import com.robotutor.nexora.common.messaging.EventPublisherImpl
import com.robotutor.nexora.common.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class UserEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<UserEvent>(eventPublisher, UserEventMapper), UserEventPublisher
