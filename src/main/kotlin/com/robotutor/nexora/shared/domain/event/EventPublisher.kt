package com.robotutor.nexora.shared.domain.event

import reactor.core.publisher.Mono

interface EventPublisher {
    fun <T : DomainEvent, R : Any> publish(event: T, transformer: () -> R): Mono<R>
    fun <T : DomainEvent> publish(event: T): Mono<Unit> {
        return publish(event) { }
    }
}