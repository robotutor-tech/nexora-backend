package com.robotutor.nexora.module.iam.infrastructure.messaging

import com.robotutor.nexora.module.iam.domain.event.IAMEvent
import com.robotutor.nexora.module.iam.domain.event.IAMEventPublisher
import com.robotutor.nexora.module.iam.infrastructure.messaging.mapper.IAMEventMapper
import com.robotutor.nexora.common.messaging.EventPublisherImpl
import com.robotutor.nexora.common.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class IAMEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<IAMEvent>(eventPublisher, IAMEventMapper), IAMEventPublisher
