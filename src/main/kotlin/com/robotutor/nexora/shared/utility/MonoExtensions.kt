package com.robotutor.nexora.shared.utility

import com.robotutor.nexora.shared.domain.exception.BaseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


fun <T : Any> createMono(content: T): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.just(content)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createMonoEmpty(): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.empty<T>()
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createMonoError(exception: BaseException): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.error<T>(exception)
            .contextWrite { context -> context.putAll(contextView) }
    }
}


fun <T : Any> createMonoError(throwable: Throwable): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.error<T>(throwable)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createFlux(content: List<T>): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.fromIterable(content)
            .contextWrite { context -> context.putAll(contextView) }
            .flatMap { createMono(it) }
    }
}

fun <T : Any> createFluxError(exception: BaseException): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.error<T>(exception)
            .contextWrite { context -> context.putAll(contextView) }
            .onErrorResume {
                createMonoError(it)
            }
    }
}

