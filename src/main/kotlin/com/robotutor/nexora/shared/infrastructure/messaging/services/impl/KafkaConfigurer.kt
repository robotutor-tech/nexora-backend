package com.robotutor.nexora.shared.infrastructure.messaging.services.impl

interface KafkaConfigurer {
    fun configureArgumentResolvers(configurer: KafkaArgumentResolverConfigurer): Unit
}
