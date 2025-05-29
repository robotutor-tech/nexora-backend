package com.robotutor.nexora.webClient

import com.robotutor.nexora.logger.LogDetails
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.logger.models.ServerWebExchangeDTO
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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

        return Mono.deferContextual { ctx ->
            val exchange = ctx.get(ServerWebExchangeDTO::class.java)
            webClient.get()
                .uri(url)
                .headers { h ->
                    h.putAll(exchange.requestDetails.headers)
                    headers.map {
                        h.set(it.key, it.value)
                    }
                }
                .retrieve()
                .bodyToMono(returnType)
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
                .contextWrite { it.putAll(ctx) }
                .doOnSubscribe {
                    logger.info(
                        LogDetails(
                            message = "Make request to Service successful",
                            additionalDetails = mapOf("method" to "GET", "url" to url)
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

        return Mono.deferContextual { ctx ->
            val exchange = ctx.get(ServerWebExchangeDTO::class.java)
            webClient
                .post()
                .uri(url)
                .headers { h ->
                    h.putAll(exchange.requestDetails.headers)
                    headers.map {
                        h.set(it.key, it.value)
                    }
                }
                .bodyValue(body)
                .retrieve()
                .bodyToMono(returnType)
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
                .contextWrite { it.putAll(ctx) }
                .doOnSubscribe {
                    logger.info(
                        LogDetails(
                            message = "Make request to Service successful",
                            additionalDetails = mapOf("method" to "POST", "url" to url)
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

        return Flux.deferContextual { ctx ->
            val exchange = ctx.get(ServerWebExchangeDTO::class.java)
            webClient.get()
                .uri(url)
                .headers { h ->
                    h.putAll(exchange.requestDetails.headers)
                    headers.map {
                        h.set(it.key, it.value)
                    }
                }
                .retrieve()
                .bodyToFlux(returnType)
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
                .contextWrite { it.putAll(ctx) }
                .doOnSubscribe {
                    logger.info(
                        LogDetails(
                            message = "Make request to Service successful",
                            additionalDetails = mapOf("method" to "GET", "url" to url)
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
        return Flux.deferContextual { ctx ->
            val exchange = ctx.get(ServerWebExchangeDTO::class.java)
            webClient
                .post()
                .uri(url)
                .headers { h ->
                    h.putAll(exchange.requestDetails.headers)
                    headers.map {
                        h.set(it.key, it.value)
                    }
                }
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(returnType)
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
                .contextWrite { it.putAll(ctx) }
                .doOnSubscribe {
                    logger.info(
                        LogDetails(
                            message = "Make request to Service successful",
                            additionalDetails = mapOf("method" to "POST", "url" to url)
                        )
                    )
                }
        }
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
}
