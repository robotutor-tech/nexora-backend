package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.shared.domain.Event
import reactor.core.publisher.Mono

interface EventPublisher<T : Event> {
    fun <R : Any> publish(event: T, transformer: () -> R): Mono<R>
    fun publish(event: T): Mono<Unit> {
        return publish(event) { }
    }

    fun <R : Any> publish(event: T, throwable: Throwable): Mono<R> {
        return publish(event).then(createMonoError(throwable))
    }
}