package com.robotutor.nexora.context.iam.infrastructure.messaging

import com.robotutor.nexora.context.iam.domain.event.IAMEvent
import com.robotutor.nexora.context.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.context.iam.infrastructure.messaging.mapper.IAMEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.EventPublisherImpl
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class IAMEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<IAMEvent>(eventPublisher, IAMEventMapper), IAMEventPublisher
