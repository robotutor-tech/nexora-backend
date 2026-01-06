package com.robotutor.nexora.module.feed.infrastructure.messaging

import com.robotutor.nexora.common.message.EventPublisherImpl
import com.robotutor.nexora.common.message.services.KafkaEventPublisher
import com.robotutor.nexora.module.feed.domain.event.FeedEvent
import com.robotutor.nexora.module.feed.domain.event.FeedEventPublisher
import com.robotutor.nexora.module.feed.infrastructure.messaging.mapper.FeedEventMapper
import org.springframework.stereotype.Service

@Service
class FeedEventPublisherImpl(
    kafkaEventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<FeedEvent>(kafkaEventPublisher, FeedEventMapper), FeedEventPublisher