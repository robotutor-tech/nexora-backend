package com.robotutor.nexora.context.device.infrastructure.messaging

import com.robotutor.nexora.context.device.domain.event.DeviceEvent
import com.robotutor.nexora.context.device.domain.event.DeviceEventPublisher
import com.robotutor.nexora.context.device.infrastructure.messaging.mapper.DeviceEventMapper
import com.robotutor.nexora.common.messaging.EventPublisherImpl
import com.robotutor.nexora.common.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class DeviceEventPublisherImpl(
    kafkaEventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<DeviceEvent>(kafkaEventPublisher, DeviceEventMapper), DeviceEventPublisher
