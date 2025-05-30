package com.robotutor.nexora.security

import org.springframework.web.server.ServerWebExchange
import com.robotutor.nexora.webClient.exceptions.BaseException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.UUID.randomUUID

const val TRACE_ID_HEADER_KEY = "x-trace-id"
fun getTraceId(exchange: ServerWebExchange): String {
    return exchange.request.headers[TRACE_ID_HEADER_KEY]?.first() ?: "missing-trace-id-${randomUUID()}"
}

fun <T : Any> createMono(content: T): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.just(content).contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createMonoError(exception: BaseException): Mono<T> {
    return Mono.deferContextual { contextView ->
        Mono.error<T>(exception).contextWrite { context -> context.putAll(contextView) }
    }
}

fun <T : Any> createFlux(content: List<T>): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.fromIterable(content).contextWrite { context -> context.putAll(contextView) }
            .flatMap { createMono(it) }
    }
}

fun <T : Any> createFluxError(exception: BaseException): Flux<T> {
    return Flux.deferContextual { contextView ->
        Flux.error<T>(exception).contextWrite { context -> context.putAll(contextView) }
    }
}

