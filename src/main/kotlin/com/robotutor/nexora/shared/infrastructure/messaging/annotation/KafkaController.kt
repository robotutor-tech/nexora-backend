package com.robotutor.nexora.shared.infrastructure.messaging.annotation

import org.springframework.stereotype.Controller

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Controller
annotation class KafkaController()
