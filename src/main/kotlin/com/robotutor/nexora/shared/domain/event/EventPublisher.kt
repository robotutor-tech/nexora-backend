package com.robotutor.nexora.shared.domain.event

import reactor.core.publisher.Mono

interface EventPublisher {
    fun <R : Any> publish(events: List<DomainEvent>, transformer: () -> R): Mono<R>
    fun publish(events: List<DomainEvent>): Mono<Unit>
}