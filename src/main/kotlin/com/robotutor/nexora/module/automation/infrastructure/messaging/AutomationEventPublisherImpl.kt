package com.robotutor.nexora.module.automation.infrastructure.messaging

import com.robotutor.nexora.common.message.EventPublisherImpl
import com.robotutor.nexora.common.message.services.KafkaEventPublisher
import com.robotutor.nexora.module.automation.domain.event.AutomationEvent
import com.robotutor.nexora.module.automation.domain.event.AutomationEventPublisher
import com.robotutor.nexora.module.automation.infrastructure.messaging.mapper.AutomationEventMapper
import org.springframework.stereotype.Service

@Service
class AutomationEventPublisherImpl(
    kafkaEventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<AutomationEvent>(kafkaEventPublisher, AutomationEventMapper), AutomationEventPublisher