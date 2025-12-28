package com.robotutor.nexora.common.http.infrastructure.client

import com.robotutor.nexora.common.observability.infrastructure.logger.LogDetails
import com.robotutor.nexora.common.observability.infrastructure.logger.Logger
import com.robotutor.nexora.common.observability.infrastructure.logger.ReactiveContext.getPremisesId
import com.robotutor.nexora.common.observability.infrastructure.logger.ReactiveContext.getTraceId
import com.robotutor.nexora.common.observability.infrastructure.logger.logOnError
import com.robotutor.nexora.common.observability.infrastructure.logger.logOnSuccess
import com.robotutor.nexora.common.observability.infrastructure.models.ServerWebExchangeDTO
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.common.security.application.filters.PREMISES_ID
import com.robotutor.nexora.common.security.application.filters.TRACE_ID
import com.robotutor.nexora.shared.domain.exception.BaseException
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Component
class WebClientWrapper(private val webClient: WebClient) {

    private val logger = Logger(this::class.java)

    fun <T> get(
        baseUrl: String,
        path: String,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {
        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return getMetaDataHeaders()
            .flatMap { metaDataHeaders ->
                webClient.get()
                    .uri(url)
                    .headers { updateHeaders(it, headers, metaDataHeaders) }
                    .retrieve()
                    .onStatus({ it.is4xxClientError }) {
                        it.bodyToMono<BaseException>()
                            .flatMap { exception -> createMonoError(exception) }
                    }
                    .bodyToMono(returnType)
//                    .retryWhen(
//                        Retry.backoff(3, Duration.ofSeconds(2))
//                            .filter { it is WebClientRequestException || it is WebClientResponseException && it.statusCode.is5xxServerError }
//                            .onRetryExhaustedThrow { _, signal -> signal.failure() }
//                    )
                    .logOnSuccess(
                        logger = logger,
                        message = "GET request to Service successful",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        skipResponseBody = skipLoggingResponseBody,
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        errorMessage = "GET request to Service failed",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "GET", "url" to url),
                                traceId = metaDataHeaders.traceId,
                                premisesId = metaDataHeaders.premisesId,
                            )
                        )
                    }
            }
    }


    fun <T> delete(
        baseUrl: String,
        path: String,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {
        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return getMetaDataHeaders()
            .flatMap { metaDataHeaders ->
                webClient.delete()
                    .uri(url)
                    .headers { updateHeaders(it, headers, metaDataHeaders) }
                    .retrieve()
                    .onStatus({ it.is4xxClientError }) {
                        it.bodyToMono<BaseException>()
                            .flatMap { exception -> createMonoError(exception) }
                    }
                    .bodyToMono(returnType)
//                    .retryWhen(
//                        Retry.backoff(3, Duration.ofSeconds(2))
//                            .filter { it is WebClientRequestException || it is WebClientResponseException && it.statusCode.is5xxServerError }
//                            .onRetryExhaustedThrow { _, signal -> signal.failure() }
//                    )
                    .logOnSuccess(
                        logger = logger,
                        message = "GET request to Service successful",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        skipResponseBody = skipLoggingResponseBody,
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        errorMessage = "GET request to Service failed",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "GET", "url" to url),
                                traceId = metaDataHeaders.traceId,
                                premisesId = metaDataHeaders.premisesId,
                            )
                        )
                    }
            }
    }

    fun <T> post(
        baseUrl: String,
        path: String,
        body: Any,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {

        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return getMetaDataHeaders()
            .flatMap { metaDataHeaders ->
                webClient
                    .post()
                    .uri(url)
                    .headers { updateHeaders(it, headers, metaDataHeaders) }
                    .bodyValue(body)
                    .retrieve()
                    .onStatus({ it.is4xxClientError }) {
                        it.bodyToMono<BaseException>()
                            .flatMap { exception -> createMonoError(exception) }
                    }
                    .bodyToMono(returnType)
//                    .retryWhen(
//                        Retry.backoff(3, Duration.ofSeconds(2))
//                            .filter { it is WebClientRequestException || it is WebClientResponseException && it.statusCode.is5xxServerError }
//                            .onRetryExhaustedThrow { _, signal -> signal.failure() }
//                    )
                    .logOnSuccess(
                        logger = logger,
                        message = "POST request to Service successful",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        skipResponseBody = skipLoggingResponseBody,
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        errorMessage = "POST request to Service failed",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "POST", "url" to url),
                                traceId = metaDataHeaders.traceId,
                                premisesId = metaDataHeaders.premisesId,
                            )
                        )
                    }
            }
    }


    fun <T> patch(
        baseUrl: String,
        path: String,
        body: Any,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Mono<T> {

        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return getMetaDataHeaders()
            .flatMap { metaDataHeaders ->
                webClient
                    .patch()
                    .uri(url)
                    .headers { updateHeaders(it, headers, metaDataHeaders) }
                    .bodyValue(body)
                    .retrieve()
                    .onStatus({ it.is4xxClientError }) {
                        it.bodyToMono<BaseException>()
                            .flatMap { exception -> createMonoError(exception) }
                    }
                    .bodyToMono(returnType)
//                    .retryWhen(
//                        Retry.backoff(3, Duration.ofSeconds(2))
//                            .filter { it is WebClientRequestException || it is WebClientResponseException && it.statusCode.is5xxServerError }
//                            .onRetryExhaustedThrow { _, signal -> signal.failure() }
//                    )
                    .logOnSuccess(
                        logger = logger,
                        message = "POST request to Service successful",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        skipResponseBody = skipLoggingResponseBody,
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        errorMessage = "POST request to Service failed",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "POST", "url" to url),
                                traceId = metaDataHeaders.traceId,
                                premisesId = metaDataHeaders.premisesId,
                            )
                        )
                    }
            }
    }


    fun <T> getFlux(
        baseUrl: String,
        path: String,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Flux<T> {
        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)

        return getMetaDataHeaders()
            .flatMapMany { metaDataHeaders ->
                webClient.get()
                    .uri(url)
                    .headers { updateHeaders(it, headers, metaDataHeaders) }
                    .retrieve()
                    .onStatus({ it.is4xxClientError }) {
                        it.bodyToMono<BaseException>()
                            .flatMap { exception -> createMonoError(exception) }
                    }
                    .bodyToFlux(returnType)
                    .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                            .filter { it is WebClientRequestException || it is WebClientResponseException && it.statusCode.is5xxServerError }
                            .onRetryExhaustedThrow { _, signal -> signal.failure() }
                    )
                    .logOnSuccess(
                        logger = logger,
                        message = "GET request to Service successful",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        skipResponseBody = skipLoggingResponseBody,
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        errorMessage = "GET request to Service failed",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "GET", "url" to url),
                                traceId = metaDataHeaders.traceId,
                                premisesId = metaDataHeaders.premisesId,
                            )
                        )
                    }
            }
    }


    fun <T> postFlux(
        baseUrl: String,
        path: String,
        body: Any,
        returnType: Class<T>,
        queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
        uriVariables: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        skipLoggingAdditionalDetails: Boolean = false,
        skipLoggingResponseBody: Boolean = true
    ): Flux<T> {
        val url = createUrlForRequest(baseUrl, path, uriVariables, queryParams)
        return getMetaDataHeaders()
            .flatMapMany { metaDataHeaders ->
                webClient
                    .post()
                    .uri(url)
                    .headers { updateHeaders(it, headers, metaDataHeaders) }
                    .bodyValue(body)
                    .retrieve()
                    .onStatus({ it.is4xxClientError }) {
                        it.bodyToMono<BaseException>()
                            .flatMap { exception -> createMonoError(exception) }
                    }
                    .bodyToFlux(returnType)
                    .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(2))
                            .filter { it is WebClientRequestException || it is WebClientResponseException && it.statusCode.is5xxServerError }
                            .onRetryExhaustedThrow { _, signal -> signal.failure() }
                    )
                    .logOnSuccess(
                        logger = logger,
                        message = "POST request to Service successful",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        skipResponseBody = skipLoggingResponseBody,
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        errorMessage = "POST request to Service failed",
                        skipAdditionalDetails = skipLoggingAdditionalDetails,
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "POST", "url" to url),
                                traceId = metaDataHeaders.traceId,
                                premisesId = metaDataHeaders.premisesId,
                            )
                        )
                    }
            }
    }

    private fun updateHeaders(
        httpHeaders: HttpHeaders,
        headers: Map<String, String>,
        metaDataHeaders: MetaDataHeaders
    ) {
        httpHeaders.putAll(metaDataHeaders.exchange.requestDetails.headers)
        headers.map {
            httpHeaders.set(it.key, it.value)
        }
        httpHeaders.set(TRACE_ID, metaDataHeaders.traceId)
        httpHeaders.set(PREMISES_ID, metaDataHeaders.premisesId)
    }


    private fun createUrlForRequest(
        baseUrl: String,
        path: String,
        uriVariables: Map<String, Any>,
        queryParams: MultiValueMap<String, String>
    ): String {
        return baseUrl + UriComponentsBuilder
            .fromPath(path)
            .uriVariables(uriVariables)
            .queryParams(queryParams)
            .build()
            .toUriString()
    }

    private fun getMetaDataHeaders(): Mono<MetaDataHeaders> {
        return Mono.deferContextual { ctx ->
            val exchange = ctx.get(ServerWebExchangeDTO::class.java)
            val traceId = exchange.requestDetails.headers.getFirst("x-trace-id") ?: getTraceId(ctx)
            val premisesId = exchange.requestDetails.headers.getFirst("x-premises-id") ?: getPremisesId(ctx)
            createMono(MetaDataHeaders(exchange, traceId, premisesId))
        }
    }
}

data class MetaDataHeaders(val exchange: ServerWebExchangeDTO, val traceId: String, val premisesId: String)