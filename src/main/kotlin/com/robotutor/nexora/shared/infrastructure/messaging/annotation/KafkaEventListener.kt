package com.robotutor.nexora.shared.infrastructure.messaging.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@Target(FUNCTION)
@Suppress("UNUSED")
annotation class KafkaEventListener(val topics: Array<String>)
