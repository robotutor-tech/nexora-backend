package com.robotutor.nexora.common.cache.annotation

import com.robotutor.nexora.common.cache.service.DefaultKeyGenerator
import com.robotutor.nexora.common.cache.service.KeyGenerator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cache(
    val name: String = "",
    val ttlInSeconds: Long = 1800,
    val keyGenerator: KClass<out KeyGenerator> = DefaultKeyGenerator::class
)
