package com.robotutor.nexora.shared.infrastructure.messaging

import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.mapper.DomainEventMapper
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import reactor.core.publisher.Mono

open class DomainEventPublisher(val eventPublisher: KafkaEventPublisher, mapper: EventMapper) : EventPublisher {
    private val domainEventMapper: EventMapper = DomainEventMapper(mapper)
    override fun <T : DomainEvent, R : Any> publish(event: T, transformer: () -> R): Mono<R> {
        val message = domainEventMapper.toEventMessage(event)
        return eventPublisher.publish(event.eventName.value, message)
            .map { transformer() }
    }
}