package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.common.security.createFlux
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.DomainEvent
import reactor.core.publisher.Mono


fun <D : DomainEvent, ID : Any, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(eventPublisher: EventPublisher<D>): Mono<T> {
    return flatMap { domain ->
        createFlux(domain.domainEvents)
            .flatMap { event -> eventPublisher.publish(event) }
            .collectList()
            .map {
                domain.clearEvents()
                domain
            }
    }
}

fun <D : DomainEvent, ID : Any, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(
    eventPublisher: EventPublisher<D>,
    aggregate: AggregateRoot<T, ID, D>
): Mono<T> {
    return flatMap { result ->
        createFlux(aggregate.domainEvents)
            .flatMap { event -> eventPublisher.publish(event) }
            .collectList()
            .map {
                aggregate.clearEvents()
                result
            }
    }
}

fun <D : DomainEvent, T : D> Mono<T>.publishEvent(eventPublisher: EventPublisher<D>): Mono<T> {
    return flatMap { event ->
        eventPublisher.publish(event).map { event }
    }
}

fun <T : DomainEvent, R : Any> Mono<R>.publishEvent(
    eventPublisher: EventPublisher<T>,
    event: T
): Mono<R> {
    return flatMap { result ->
        eventPublisher.publish(event).map { result }
    }
}

fun <D : DomainEvent, ID : Any, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(
    eventPublisher: EventPublisher<D>,
    events: List<D>
): Mono<T> {
    return flatMap { result ->
        createFlux(events)
            .flatMap { event -> eventPublisher.publish(event) }
            .collectList()
            .map { result }
    }
}
