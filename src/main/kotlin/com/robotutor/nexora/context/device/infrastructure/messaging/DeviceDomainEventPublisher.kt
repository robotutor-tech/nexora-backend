package com.robotutor.nexora.context.device.infrastructure.messaging

import com.robotutor.nexora.context.device.domain.event.DeviceDomainEvent
import com.robotutor.nexora.context.device.infrastructure.messaging.mapper.DeviceDomainEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class DeviceDomainEventPublisher(
    kafkaEventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<DeviceDomainEvent>(kafkaEventPublisher, DeviceDomainEventMapper)