package com.robotutor.nexora.module.identity.infrastructure.messaging

import com.robotutor.nexora.module.identity.domain.event.IAMEvent
import com.robotutor.nexora.module.identity.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.identity.infrastructure.messaging.mapper.IAMEventMapper
import com.robotutor.nexora.shared.message.EventPublisherImpl
import com.robotutor.nexora.shared.message.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class IAMEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<IAMEvent>(eventPublisher, IAMEventMapper), IAMEventPublisher
