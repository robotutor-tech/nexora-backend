package com.robotutor.nexora.shared.application.cache.annotation

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Cached(
    val cacheName: String,
    val key: String,
    val failOnKeyEvaluationError: Boolean = true,
)

