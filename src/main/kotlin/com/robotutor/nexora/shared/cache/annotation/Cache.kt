package com.robotutor.nexora.shared.cache.annotation

import com.robotutor.nexora.shared.cache.service.DefaultKeyGenerator
import com.robotutor.nexora.shared.cache.service.KeyGenerator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cache(
    val name: String = "",
    val ttlInSeconds: Long = 1800,
    val keyGenerator: KClass<out KeyGenerator> = DefaultKeyGenerator::class
)
