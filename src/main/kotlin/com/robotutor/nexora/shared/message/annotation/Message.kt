package com.robotutor.nexora.shared.message.annotation

import kotlin.annotation.AnnotationRetention.RUNTIME

@Retention(RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Message
