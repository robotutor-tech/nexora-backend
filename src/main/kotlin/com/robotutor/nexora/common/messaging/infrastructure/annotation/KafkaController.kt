package com.robotutor.nexora.common.messaging.infrastructure.annotation

import org.springframework.stereotype.Controller

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Controller
annotation class KafkaController()
