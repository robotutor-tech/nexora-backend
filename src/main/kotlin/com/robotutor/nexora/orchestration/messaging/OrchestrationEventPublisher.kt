package com.robotutor.nexora.orchestration.messaging

import com.robotutor.nexora.orchestration.messaging.event.OrchestrationEvent
import com.robotutor.nexora.orchestration.messaging.mapper.OrchestrationEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class OrchestrationEventPublisher(
    kafkaEventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<OrchestrationEvent>(kafkaEventPublisher, OrchestrationEventMapper)