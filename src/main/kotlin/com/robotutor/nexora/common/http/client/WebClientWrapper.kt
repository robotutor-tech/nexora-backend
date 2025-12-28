package com.robotutor.nexora.common.http.client

import com.robotutor.nexora.shared.application.logger.LogDetails
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.X_PREMISES_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.getCorrelationId
import com.robotutor.nexora.shared.application.logger.ReactiveContext.getPremisesId
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.exception.BaseException
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
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
                        additionalDetails = mapOf("method" to "GET", "path" to url),
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        message = "GET request to Service failed",
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "GET", "url" to url),
                                premisesId = metaDataHeaders.premisesId,
                                correlationId = metaDataHeaders.correlationId,
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
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        message = "GET request to Service failed",
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "GET", "url" to url),
                                premisesId = metaDataHeaders.premisesId,
                                correlationId = metaDataHeaders.correlationId,
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
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        message = "POST request to Service failed",
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "POST", "url" to url),
                                premisesId = metaDataHeaders.premisesId,
                                correlationId = metaDataHeaders.correlationId,
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
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        message = "POST request to Service failed",
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "POST", "url" to url),
                                premisesId = metaDataHeaders.premisesId,
                                correlationId = metaDataHeaders.correlationId,
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
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        message = "GET request to Service failed",
                        additionalDetails = mapOf("method" to "GET", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "GET", "url" to url),
                                premisesId = metaDataHeaders.premisesId,
                                correlationId = metaDataHeaders.correlationId,
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
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .logOnError(
                        logger = logger,
                        errorCode = "API_FAILURE",
                        message = "POST request to Service failed",
                        additionalDetails = mapOf("method" to "POST", "path" to url)
                    )
                    .doOnSubscribe {
                        logger.info(
                            LogDetails(
                                message = "Make request to Service successful",
                                additionalDetails = mapOf("method" to "POST", "url" to url),
                                premisesId = metaDataHeaders.premisesId,
                                correlationId = metaDataHeaders.correlationId,
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
        metaDataHeaders.headers.remove(HttpHeaders.CONTENT_LENGTH)
        httpHeaders.putAll(metaDataHeaders.headers)
        headers.map {
            httpHeaders.set(it.key, it.value)
        }
        httpHeaders.set(X_PREMISES_ID, metaDataHeaders.premisesId)
        httpHeaders.set(CORRELATION_ID, metaDataHeaders.premisesId)
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
            val headers = ctx.get(HttpHeaders::class.java)
            val premisesId = getPremisesId(ctx).value
            val correlationId = getCorrelationId(ctx)
            createMono(MetaDataHeaders(headers, premisesId, correlationId))
        }
    }
}

data class MetaDataHeaders(
    val headers: HttpHeaders,
    val premisesId: String,
    val correlationId: String
)