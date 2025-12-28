package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.shared.application.reactive.ContextDataResolver
import com.robotutor.nexora.shared.application.reactive.TraceData
import com.robotutor.nexora.shared.domain.vo.PremisesId
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
        ContextDataResolver.getTraceData()
            .map { traceData ->
                val logDetails = LogDetails(
                    message = message,
                    additionalDetails = additionalDetails,
                    premisesId = traceData.premisesId.value,
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
        ContextDataResolver.getTraceData()
            .flatMap { traceData ->
                val logDetails = LogDetails(
                    message = message,
                    additionalDetails = additionalDetails,
                    errorCode = errorCode,
                    premisesId = traceData.premisesId.value,
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
    var traceData = TraceData("missing-correlation-id", PremisesId("missing-premises-id"))
    return flatMap { result ->
        if (!hasElements.get()) {
            ContextDataResolver.getTraceData()
                .map {
                    hasElements.set(true)
                    traceData = it
                    result
                }
        } else createMono(result as Any) as Mono<T>
    }
        .doOnComplete {
            if (hasElements.get()) {
                logger.info(
                    LogDetails(
                        message = message,
                        additionalDetails = additionalDetails,
                        premisesId = traceData.premisesId.value,
                        correlationId = traceData.correlationId
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
        ContextDataResolver.getTraceData()
            .map { traceData ->
                logger.error(
                    LogDetails(
                        message = message,
                        additionalDetails = additionalDetails,
                        errorCode = errorCode,
                        premisesId = traceData.premisesId.value,
                        correlationId = traceData.correlationId
                    ),
                    throwable
                )
            }
            .flatMap { createMonoError(throwable) }
    }
}
