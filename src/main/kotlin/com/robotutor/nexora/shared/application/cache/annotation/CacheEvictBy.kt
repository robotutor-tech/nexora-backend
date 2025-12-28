package com.robotutor.nexora.shared.application.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheEvictBy(
    val cacheName: String,
    val key: String,
    val beforeInvocation: Boolean = false,
)

