package com.robotutor.nexora.shared.domain.event

import reactor.core.publisher.Mono

interface EventPublisher<T : DomainEvent> {
    fun <R : Any> publish(event: T, transformer: () -> R): Mono<R>
    fun publish(event: T): Mono<Unit> {
        return publish(event) { }
    }
}