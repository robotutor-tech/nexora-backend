package com.robotutor.nexora.shared.utility

import com.robotutor.nexora.shared.domain.exception.BaseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


fun <T> createMono(content: T): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.justOrEmpty<T>(content)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T> createMonoEmpty(): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.empty<T>()
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T> createMonoError(exception: BaseException): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.error<T>(exception)
            .contextWrite { context -> context.putAll(contextView) }
    }
}


fun <T> createMonoError(throwable: Throwable): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.error<T>(throwable)
            .contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T> createFlux(content: List<T>): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.fromIterable(content)
            .contextWrite { context -> context.putAll(contextView) }
            .flatMap { createMono(it) }
    }
}

fun <T> createFluxError(exception: BaseException): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.error<T>(exception)
            .contextWrite { context -> context.putAll(contextView) }
            .onErrorResume { createMonoError(it) }
    }
}

fun <T> createFluxError(throwable: Throwable): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.error<T>(throwable)
            .contextWrite { context -> context.putAll(contextView) }
            .onErrorResume { createMonoError(it) }
    }
}

fun <T> Mono<T>.required(exception: BaseException): Mono<T> {
    return switchIfEmpty(createMonoError(exception))
}

