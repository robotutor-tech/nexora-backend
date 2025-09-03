package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.common.security.createFlux
import reactor.core.publisher.Mono

fun <D : DomainEvent, T : DomainAggregate<D>> Mono<T>.publishEvents(eventPublisher: EventPublisher<D>): Mono<T> {
    return flatMap { domain ->
        createFlux(domain.getDomainEvents())
            .flatMap { event -> eventPublisher.publish(event) }
            .collectList()
            .map {
                domain.clearDomainEvents()
                domain
            }
    }
}

fun <D : DomainEvent, T : Any> Mono<T>.publishEvents(
    eventPublisher: EventPublisher<D>,
    domainAggregate: DomainAggregate<D>
): Mono<T> {
    return flatMap { result ->
        createFlux(domainAggregate.getDomainEvents())
            .flatMap { event -> eventPublisher.publish(event) }
            .collectList()
            .map {
                domainAggregate.clearDomainEvents()
                result
            }
    }
}

fun <D : DomainEvent, T : D> Mono<T>.publishEvent(eventPublisher: EventPublisher<D>): Mono<T> {
    return flatMap { event ->
        eventPublisher.publish(event).map { event }
    }
}

fun <T : DomainEvent, R : Any> Mono<R>.publishEvent(eventPublisher: EventPublisher<T>, event: T): Mono<R> {
    return flatMap { result ->
        eventPublisher.publish(event).map { result }
    }
}

fun <D : DomainEvent, T : Any> Mono<T>.publishEvents(eventPublisher: EventPublisher<D>, events: List<D>): Mono<T> {
    return flatMap { result ->
        createFlux(events)
            .flatMap { event -> eventPublisher.publish(event) }
            .collectList()
            .map { result }
    }
}
