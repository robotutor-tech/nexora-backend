package com.robotutor.nexora.shared.application.observability

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import java.util.concurrent.atomic.AtomicBoolean

fun <T> Mono<T>.logOnSuccess(
    logger: AppLogger,
    message: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    skipAdditionalDetails: Boolean = false,
    skipResponseBody: Boolean = true,
): Mono<T> {
    return doOnEach { signal: Signal<T> ->
        if (signal.isOnNext) {
            logger.info(
                message = message,
                additionalDetails = additionalDetails,
                searchableFields = searchableFields,
                skipAdditionalDetails = skipAdditionalDetails,
                skipResponseBody = skipResponseBody,
            )
        }
    }
}

fun <T> Mono<T>.logOnError(
    logger: AppLogger,
    errorMessage: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    errorCode: String? = null,
    skipAdditionalDetails: Boolean = false,
): Mono<T> {
    return doOnEach { signal: Signal<T> ->
        if (signal.isOnError) {
            logger.error(
                errorMessage = errorMessage,
                throwable = signal.throwable,
                errorCode = errorCode,
                additionalDetails = additionalDetails,
                searchableFields = searchableFields,
                skipAdditionalDetails = skipAdditionalDetails,
            )
        }
    }
}

fun <T> Flux<T>.logOnSuccess(
    logger: AppLogger,
    message: String,
    additionalDetails: Map<String, Any?> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    skipAdditionalDetails: Boolean = false,
    skipResponseBody: Boolean = true,
): Flux<T> {
    val hasElements = AtomicBoolean(false)
    return doOnNext { hasElements.set(true) }
        .doOnComplete {
            if (hasElements.get()) {
                logger.info(
                    message = message,
                    additionalDetails = additionalDetails,
                    searchableFields = searchableFields,
                    skipAdditionalDetails = skipAdditionalDetails,
                    skipResponseBody = skipResponseBody,
                )
            }
        }
}

fun <T> Flux<T>.logOnError(
    logger: AppLogger,
    errorMessage: String,
    errorCode: String? = null,
    additionalDetails: Map<String, Any?> = emptyMap(),
    searchableFields: Map<String, Any?> = emptyMap(),
    skipAdditionalDetails: Boolean = false,
): Flux<T> {
    return doOnError { throwable ->
        logger.error(
            errorMessage = errorMessage,
            throwable = throwable,
            errorCode = errorCode,
            additionalDetails = additionalDetails,
            searchableFields = searchableFields,
            skipAdditionalDetails = skipAdditionalDetails,
        )
    }
}

