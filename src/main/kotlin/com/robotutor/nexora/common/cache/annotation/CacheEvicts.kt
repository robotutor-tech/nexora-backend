package com.robotutor.nexora.common.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CacheEvicts(
    val evicts: Array<String>
)
