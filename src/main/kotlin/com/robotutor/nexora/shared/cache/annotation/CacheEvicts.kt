package com.robotutor.nexora.shared.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheEvicts(
    val evicts: Array<String>
)
