package com.robotutor.nexora.shared.adapters.messaging.services

import com.robotutor.nexora.common.security.createFlux
import com.robotutor.nexora.shared.domain.event.DomainEvent
import com.robotutor.nexora.shared.domain.event.EventHandler
import com.robotutor.nexora.shared.domain.event.EventPublisher
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InMemoryEventPublisher(applicationContext: ApplicationContext) : EventPublisher {
    private val handlers = applicationContext.getBeansOfType(EventHandler::class.java).values.toList()

    override fun <R : Any> publish(events: List<DomainEvent>, transformer: () -> R): Mono<R> {
        return createFlux(events)
            .flatMap { event ->
                createFlux(handlers)
                    .filter { it.eventType.isInstance(event) }
                    .flatMap { handler ->
                        @Suppress("UNCHECKED_CAST")
                        (handler as EventHandler<DomainEvent>).handle(event)
                    }
            }
            .collectList()
            .map { transformer() }
    }

    override fun publish(events: List<DomainEvent>): Mono<Unit> {
        return publish(events) { }
    }
}