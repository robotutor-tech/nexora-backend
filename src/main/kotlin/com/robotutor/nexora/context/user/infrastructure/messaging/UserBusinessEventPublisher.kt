package com.robotutor.nexora.context.user.infrastructure.messaging

import com.robotutor.nexora.context.user.domain.event.UserBusinessEvent
import com.robotutor.nexora.context.user.infrastructure.messaging.mapper.UserBusinessEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.BusinessEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class UserBusinessEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : BusinessEventPublisher<UserBusinessEvent>(eventPublisher, UserBusinessEventMapper)
