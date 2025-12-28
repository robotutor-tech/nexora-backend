package com.robotutor.nexora.shared.application.cache.annotation

/**
 * Application-level cache eviction annotation.
 *
 * DDD intent:
 * - Application code expresses eviction intent.
 * - Infrastructure decides how/where (Redis, local, etc.).
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheEvictBy(
    val cacheName: String,
    /**
     * SpEL expression evaluated against method arguments. Example:
     * - "T(com.robotutor.nexora.common.cache.application.CacheKeys).userById(#command.userId.value)"
     */
    val key: String,
    val beforeInvocation: Boolean = false,
)

