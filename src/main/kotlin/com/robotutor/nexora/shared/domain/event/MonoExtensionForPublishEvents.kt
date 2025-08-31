package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.common.security.createFlux
import com.robotutor.nexora.common.security.createMono
import reactor.core.publisher.Mono

fun <T : DomainAggregate> Mono<T>.publishEvents(eventPublisher: EventPublisher): Mono<T> {
    return flatMap { domain ->
        createFlux(domain.getDomainEvents())
            .flatMap { event ->
                eventPublisher.publish(event)
            }
            .collectList()
            .map {
                domain.clearDomainEvents()
            }
            .map { domain }
    }
}

fun <T : Any> Mono<T>.publishEvents(eventPublisher: EventPublisher, domainAggregate: DomainAggregate): Mono<T> {
    return flatMap { result ->
        createMono(domainAggregate)
            .publishEvents(eventPublisher)
            .map { result }
    }
}

fun <T : DomainEvent> Mono<T>.publishEvent(eventPublisher: EventPublisher): Mono<T> {
    return flatMap { event -> publishEvents(eventPublisher, listOf(event)) }
}

fun <T : Any> Mono<T>.publishEvents(eventPublisher: EventPublisher, events: List<DomainEvent>): Mono<T> {
    val domainAggregate = DomainAggregate(events.toMutableList())
    return publishEvents(eventPublisher, domainAggregate)
}
