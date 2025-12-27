package com.robotutor.nexora.shared.infrastructure.persistence.repository

import org.springframework.dao.OptimisticLockingFailureException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

fun <T> Mono<T>.retryOptimisticLockingFailure(): Mono<T> {
    return retryWhen(
        Retry.fixedDelay(5, Duration.ofMillis(500))
            .filter { it is OptimisticLockingFailureException })
}

fun <T> Flux<T>.retryOptimisticLockingFailure(): Flux<T> {
    return retryWhen(
        Retry.fixedDelay(5, Duration.ofMillis(500))
            .filter { it is OptimisticLockingFailureException })
}