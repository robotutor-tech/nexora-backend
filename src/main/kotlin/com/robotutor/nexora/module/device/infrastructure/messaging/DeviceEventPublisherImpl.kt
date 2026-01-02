package com.robotutor.nexora.module.device.infrastructure.messaging

import com.robotutor.nexora.module.device.domain.event.DeviceEvent
import com.robotutor.nexora.module.device.domain.event.DeviceEventPublisher
import com.robotutor.nexora.module.device.infrastructure.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.common.messaging.EventPublisherImpl
import com.robotutor.nexora.common.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class DeviceEventPublisherImpl(
    kafkaEventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<DeviceEvent>(kafkaEventPublisher, DeviceEventMapper), DeviceEventPublisher
