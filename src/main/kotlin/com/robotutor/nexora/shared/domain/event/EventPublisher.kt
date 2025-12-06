package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.shared.domain.DomainEvent
import reactor.core.publisher.Mono

interface EventPublisher<T : DomainEvent> {
    fun <R : Any> publish(event: T, transformer: () -> R): Mono<R>
    fun publish(event: T): Mono<Unit> {
        return publish(event) { }
    }

    fun <R : Any> publish(event: T, throwable: Throwable): Mono<R> {
        return publish(event).then(createMonoError(throwable))
    }
}