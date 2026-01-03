package com.robotutor.nexora.common.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cache(
    val name: String = "",
    val ttlInSeconds: Long = 60
)
