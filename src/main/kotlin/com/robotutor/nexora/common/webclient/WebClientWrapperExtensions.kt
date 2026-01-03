package com.robotutor.nexora.common.webclient

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

inline fun <reified T : Any> WebClientWrapper.get(
    baseUrl: String,
    path: String,
    queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
    uriVariables: Map<String, Any> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Mono<T> = this.get(baseUrl, path, T::class.java, queryParams, uriVariables, headers)


inline fun <reified T : Any> WebClientWrapper.delete(
    baseUrl: String,
    path: String,
    queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
    uriVariables: Map<String, Any> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Mono<T> = this.delete(baseUrl, path, T::class.java, queryParams, uriVariables, headers)

inline fun <reified T : Any> WebClientWrapper.post(
    baseUrl: String,
    path: String,
    body: Any,
    queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
    uriVariables: Map<String, Any> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Mono<T> = this.post(baseUrl, path, body, T::class.java, queryParams, uriVariables, headers)


inline fun <reified T : Any> WebClientWrapper.patch(
    baseUrl: String,
    path: String,
    body: Any,
    queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
    uriVariables: Map<String, Any> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Mono<T> = this.patch(baseUrl, path, body, T::class.java, queryParams, uriVariables, headers)


inline fun <reified T : Any> WebClientWrapper.getFlux(
    baseUrl: String,
    path: String,
    queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
    uriVariables: Map<String, Any> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Flux<T> = this.getFlux(baseUrl, path, T::class.java, queryParams, uriVariables, headers)


inline fun <reified T : Any> WebClientWrapper.postFlux(
    baseUrl: String,
    path: String,
    body: Any,
    queryParams: MultiValueMap<String, String> = LinkedMultiValueMap(),
    uriVariables: Map<String, Any> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Flux<T> = this.postFlux(baseUrl, path, body, T::class.java, queryParams, uriVariables, headers)

