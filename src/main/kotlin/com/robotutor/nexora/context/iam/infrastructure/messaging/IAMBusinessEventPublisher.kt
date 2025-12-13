package com.robotutor.nexora.context.iam.infrastructure.messaging

import com.robotutor.nexora.context.iam.domain.event.IAMBusinessEvent
import com.robotutor.nexora.context.iam.domain.event.IAMDomainEvent
import com.robotutor.nexora.context.iam.infrastructure.messaging.mapper.IAMBusinessEventMapper
import com.robotutor.nexora.context.iam.infrastructure.messaging.mapper.IAMDomainEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.BusinessEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class IAMBusinessEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : BusinessEventPublisher<IAMBusinessEvent>(eventPublisher, IAMBusinessEventMapper)