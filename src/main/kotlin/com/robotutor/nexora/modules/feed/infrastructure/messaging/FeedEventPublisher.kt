package com.robotutor.nexora.modules.feed.infrastructure.messaging

import com.robotutor.nexora.modules.feed.domain.event.FeedEvent
import com.robotutor.nexora.modules.feed.infrastructure.messaging.mapper.FeedEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import org.springframework.stereotype.Service

@Service
class FeedEventPublisher(
    eventPublisher: KafkaEventPublisher,
) : DomainEventPublisher<FeedEvent>(eventPublisher, FeedEventMapper)
