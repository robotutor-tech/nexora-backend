package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.shared.utility.createFlux
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.DomainEvent
import com.robotutor.nexora.shared.domain.Event
import com.robotutor.nexora.shared.domain.vo.Identifier
import com.robotutor.nexora.shared.infrastructure.messaging.DomainEventPublisher
import com.robotutor.nexora.shared.utility.createMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <D : DomainEvent, ID : Identifier, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(eventPublisher: DomainEventPublisher<D>): Mono<T> {
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

fun <D : DomainEvent, ID : Identifier, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(
    eventPublisher: DomainEventPublisher<D>,
    aggregate: AggregateRoot<T, ID, D>
): Mono<T> {
    return flatMap { result ->
        createFlux(aggregate.domainEvents)
            .flatMap { event ->
                eventPublisher.publish(event)
            }
            .collectList()
            .map {
                aggregate.clearEvents()
                result
            }
    }
}

fun <D : DomainEvent, ID : Identifier, T : AggregateRoot<T, ID, D>> Flux<T>.publishEvents(eventPublisher: DomainEventPublisher<D>): Flux<T> {
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

//fun <D : DomainEvent, ID : Identifier, T : AggregateRoot<T, ID, D>> Mono<T>.publishEvents(
//    eventPublisher: DomainEventPublisher<D>,
//    events: List<D>
//): Mono<T> {
//    return flatMap { result ->
//        createFlux(events)
//            .flatMap { event -> eventPublisher.publish(event) }
//            .collectList()
//            .map { result }
//    }
//}

//fun <D : DomainEvent, ID : Identifier, T : AggregateRoot<T, ID, D>> Flux<T>.publishEvents(
//    eventPublisher: DomainEventPublisher<D>,
//    events: List<D>
//): Flux<T> {
//    return collectList()
//        .flatMapMany { results ->
//            createFlux(events)
//                .flatMap { event -> eventPublisher.publish(event) }
//                .collectList()
//                .flatMapMany { Flux.fromIterable(results) }
//        }
//}

fun <D : DomainEvent, ID : Identifier, T : AggregateRoot<T, ID, D>> Flux<T>.publishEvents(
    eventPublisher: DomainEventPublisher<D>,
    aggregates: List<T>
): Flux<T> {
    return collectList()
        .flatMapMany { result ->
            createFlux(aggregates)
                .publishEvents(eventPublisher)
                .collectList()
                .flatMapMany { createFlux(result) }
        }

}

fun <D : Event, T : D> Mono<T>.publishEvent(eventPublisher: EventPublisher<D>): Mono<T> {
    return flatMap { event ->
        eventPublisher.publish(event).map { event }
    }
}

fun <T : Event, R : Any> Mono<R>.publishEvent(eventPublisher: EventPublisher<T>, event: T): Mono<R> {
    return flatMap { result ->
        eventPublisher.publish(event).map { result }
    }
}
