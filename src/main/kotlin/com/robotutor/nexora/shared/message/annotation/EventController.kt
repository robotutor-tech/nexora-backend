package com.robotutor.nexora.shared.message.annotation

import org.springframework.stereotype.Controller

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Controller
annotation class EventController
