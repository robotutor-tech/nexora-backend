package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.shared.application.service.ContextDataResolver
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.UserData
import reactor.core.publisher.Mono

abstract class EventHandler<T : DomainEvent>(val eventType: Class<T>) {
    open fun handle(event: T): Mono<Any> {
        return ContextDataResolver.getUserData()
            .flatMap { userData -> handle(event, userData) }
            .flatMap { ContextDataResolver.getActorData() }
            .flatMap { actorData -> handle(event, actorData) }
    }

    open fun handle(event: T, actorData: ActorData): Mono<Any> {
        return createMono(event)
    }

    open fun handle(event: T, userData: UserData): Mono<Any> {
        return createMono(event)
    }
}