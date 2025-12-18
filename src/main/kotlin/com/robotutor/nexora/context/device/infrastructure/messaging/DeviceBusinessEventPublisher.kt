package com.robotutor.nexora.context.device.infrastructure.messaging

import com.robotutor.nexora.context.device.domain.event.DeviceBusinessEvent
import com.robotutor.nexora.context.device.infrastructure.messaging.mapper.DeviceBusinessEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.BusinessEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class DeviceBusinessEventPublisher(
    kafkaEventPublisher: KafkaEventPublisher,
) : BusinessEventPublisher<DeviceBusinessEvent>(kafkaEventPublisher, DeviceBusinessEventMapper)