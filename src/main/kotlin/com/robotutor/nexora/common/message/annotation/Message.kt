package com.robotutor.nexora.common.message.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Message
