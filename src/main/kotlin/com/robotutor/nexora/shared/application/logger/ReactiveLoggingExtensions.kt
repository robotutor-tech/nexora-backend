package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.shared.context.ContextData
import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicBoolean

fun <T> Mono<T>.logOnSuccess(
    logger: Logger,
    message: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
    level: LogLevel = LogLevel.INFO,
): Mono<T> {
    return flatMap { value ->
        ReactiveContext.getContextData()
            .map { traceData ->
                val logDetails = LogDetails(
                    message = message,
                    additionalDetails = additionalDetails,
                    correlationId = traceData.correlationId
                )
                logger.log(logDetails, level)
                value
            }
    }
}

fun <T> Mono<T>.logOnError(
    logger: Logger,
    message: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
    errorCode: String? = null,
    level: LogLevel = LogLevel.ERROR
): Mono<T> {
    return onErrorResume { throwable ->
        ReactiveContext.getContextData()
            .flatMap { traceData ->
                val logDetails = LogDetails(
                    message = message,
                    additionalDetails = additionalDetails,
                    errorCode = errorCode,
                    correlationId = traceData.correlationId
                )
                logger.log(logDetails, level, throwable)
                createMonoError(throwable)
            }
    }
}

fun <T> Flux<T>.logOnSuccess(
    logger: Logger,
    message: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
    level: LogLevel = LogLevel.INFO,
): Flux<T> {
    val hasElements = AtomicBoolean(false)
    var contextData = ContextData("missing-correlation-id", null)
    return flatMap { result ->
        if (!hasElements.get()) {
            ReactiveContext.getContextData()
                .map {
                    hasElements.set(true)
                    contextData = it
                    result
                }
        } else {
            @Suppress("UNCHECKED_CAST")
            createMono(result as Any) as Mono<T>
        }
    }
        .doOnComplete {
            if (hasElements.get()) {
                val details = LogDetails(
                    message = message,
                    additionalDetails = additionalDetails,
                    correlationId = contextData.correlationId,
                    principalData = contextData.principalData

                )
                logger.log(details, level)
            }
        }
}

fun <T> Flux<T>.logOnError(
    logger: Logger,
    message: String,
    errorCode: String? = null,
    additionalDetails: Map<String, Any?> = emptyMap(),
    level: LogLevel = LogLevel.ERROR,
): Flux<T> {
    return onErrorResume { throwable ->
        ReactiveContext.getContextData()
            .map { contextData ->
                val details = LogDetails(
                    message = message,
                    additionalDetails = additionalDetails,
                    errorCode = errorCode,
                    correlationId = contextData.correlationId,
                    principalData = contextData.principalData

                )
                logger.log(details, level, throwable)
            }
            .flatMap { createMonoError(throwable) }
    }
}
