package com.robotutor.nexora.shared.message.annotation

import com.robotutor.nexora.shared.message.config.EventName
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(RUNTIME)
@Target(FUNCTION)
annotation class EventListener(val topics: Array<EventName>)
