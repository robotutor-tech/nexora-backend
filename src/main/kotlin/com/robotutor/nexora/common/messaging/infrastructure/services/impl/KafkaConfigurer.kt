package com.robotutor.nexora.common.messaging.infrastructure.services.impl

interface KafkaConfigurer {
    fun configureArgumentResolvers(configurer: KafkaArgumentResolverConfigurer): Unit
}
