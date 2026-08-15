package com.robotutor.nexora.module.identity.infrastructure.messaging

import com.robotutor.nexora.module.identity.domain.event.IdentityEvent
import com.robotutor.nexora.module.identity.domain.event.IdentityEventPublisher
import com.robotutor.nexora.module.identity.infrastructure.messaging.mapper.IdentityEventMapper
import com.robotutor.nexora.shared.message.EventPublisherImpl
import com.robotutor.nexora.shared.message.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class IdentityEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<IdentityEvent>(eventPublisher, IdentityEventMapper), IdentityEventPublisher
