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
    additionalDetails: Map<String, Any?> = emptyMap()
): Mono<T> {
    return flatMap { value ->
        ReactiveContext.getContextData()
            .map { traceData ->
                val logDetails = LogDetails(
                    message = message,
                    additionalDetails = additionalDetails,
                    correlationId = traceData.correlationId
                )
                logger.info(logDetails)
                value
            }
    }
}

fun <T> Mono<T>.logOnError(
    logger: Logger,
    message: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
    errorCode: String? = null,
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
                logger.error(logDetails, throwable)
                createMonoError(throwable)
            }
    }
}

fun <T> Flux<T>.logOnSuccess(
    logger: Logger,
    message: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
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
                logger.info(
                    LogDetails(
                        message = message,
                        additionalDetails = additionalDetails,
                        correlationId = contextData.correlationId,
                        principalData = contextData.principalData

                    )
                )
            }
        }
}

fun <T> Flux<T>.logOnError(
    logger: Logger,
    message: String,
    errorCode: String? = null,
    additionalDetails: Map<String, Any?> = emptyMap(),
): Flux<T> {
    return onErrorResume { throwable ->
        ReactiveContext.getContextData()
            .map { contextData ->
                logger.error(
                    LogDetails(
                        message = message,
                        additionalDetails = additionalDetails,
                        errorCode = errorCode,
                        correlationId = contextData.correlationId,
                        principalData = contextData.principalData

                    ),
                    throwable
                )
            }
            .flatMap { createMonoError(throwable) }
    }
}
