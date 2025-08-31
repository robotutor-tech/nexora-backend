package com.robotutor.nexora.modules.device.infrastructure.messaging

import com.robotutor.nexora.modules.device.application.event.DeviceEventPublisher
import com.robotutor.nexora.modules.device.infrastructure.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class KafkaDeviceEventPublisher(
    kafkaEventPublisher: KafkaEventPublisher, deviceEventMapper: DeviceEventMapper
) : DomainEventPublisher(kafkaEventPublisher, deviceEventMapper), DeviceEventPublisher