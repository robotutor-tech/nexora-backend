package com.robotutor.nexora.utils

import org.springframework.dao.OptimisticLockingFailureException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

fun <T> Mono<T>.retryOptimisticLockingFailure(): Mono<T> {
    return retryWhen(
        Retry.fixedDelay(5, Duration.ofMillis(500))
            .filter { it is OptimisticLockingFailureException })
}