package com.robotutor.nexora.shared.application.cache.annotation

/**
 * Application-level caching annotation.
 *
 * DDD intent:
 * - Application code expresses caching intent using this annotation (no Spring @Cacheable leaking into use cases).
 * - Infrastructure provides the implementation via an Aspect and Redis.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cached(
    val cacheName: String,
    /**
     * SpEL expression evaluated against method arguments. Example:
     * - "T(com.robotutor.nexora.common.cache.application.CacheKeys).userById(#query.principalId.value)"
     */
    val key: String,
    /**
     * If true, wraps failures from key resolution into an IllegalArgumentException.
     */
    val failOnKeyEvaluationError: Boolean = true,
)

