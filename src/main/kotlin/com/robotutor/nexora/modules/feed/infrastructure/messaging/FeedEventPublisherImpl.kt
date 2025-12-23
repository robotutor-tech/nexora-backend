package com.robotutor.nexora.modules.feed.infrastructure.messaging

import com.robotutor.nexora.modules.feed.domain.event.FeedEvent
import com.robotutor.nexora.modules.feed.domain.event.FeedEventPublisher
import com.robotutor.nexora.modules.feed.infrastructure.messaging.mapper.FeedEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.EventPublisherImpl
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class FeedEventPublisherImpl(
    eventPublisher: KafkaEventPublisher,
) : EventPublisherImpl<FeedEvent>(eventPublisher, FeedEventMapper), FeedEventPublisher
