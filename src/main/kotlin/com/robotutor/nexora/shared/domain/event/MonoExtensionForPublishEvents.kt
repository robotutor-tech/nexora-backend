package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import reactor.core.publisher.Mono

fun <T : DomainAggregate> Mono<T>.publishEvents(): Mono<T> {
    return flatMap { domain ->
        ContextDataResolver.getEventPublisher()
            .flatMap { eventPublisher ->
                eventPublisher.publish(domain.getDomainEvents()) {
                    domain.clearDomainEvents()
                }
            }
            .map { domain }
    }
}

fun <T : Any> Mono<T>.publishEvents(domainAggregate: DomainAggregate): Mono<T> {
    return flatMap { result ->
        createMono(domainAggregate)
            .publishEvents()
            .map { result }
    }
}

fun <T : Any> Mono<T>.publishEvent(event: DomainEvent): Mono<T> {
    return publishEvents(listOf(event))
}

fun <T : DomainEvent> Mono<T>.publishEvent(): Mono<T> {
    return flatMap { event -> publishEvents(listOf(event)) }
}

fun <T : Any> Mono<T>.publishEvents(events: List<DomainEvent>): Mono<T> {
    val domainAggregate = DomainAggregate(events.toMutableList())
    return publishEvents(domainAggregate)
}