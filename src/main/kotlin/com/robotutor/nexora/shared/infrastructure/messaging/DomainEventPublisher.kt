package com.robotutor.nexora.shared.infrastructure.messaging

import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.event.EventMapper
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.infrastructure.messaging.services.KafkaEventPublisher
import reactor.core.publisher.Mono

open class DomainEventPublisher<T : DomainEvent>(
    val eventPublisher: KafkaEventPublisher, val mapper: EventMapper<T>
) : EventPublisher<T> {
    override fun <R : Any> publish(event: T, transformer: () -> R): Mono<R> {
        val message = mapper.toEventMessage(event)
        return eventPublisher.publish(event.eventName, message)
            .map { transformer() }
    }
}