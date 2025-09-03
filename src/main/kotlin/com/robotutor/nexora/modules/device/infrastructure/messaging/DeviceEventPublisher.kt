package com.robotutor.nexora.modules.device.infrastructure.messaging

import com.robotutor.nexora.modules.device.domain.event.DeviceEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class KafkaDeviceEventPublisher(
    kafkaEventPublisher: KafkaEventPublisher, mapper: EventMapper<DeviceEvent>,
) : DomainEventPublisher<DeviceEvent>(kafkaEventPublisher, mapper)