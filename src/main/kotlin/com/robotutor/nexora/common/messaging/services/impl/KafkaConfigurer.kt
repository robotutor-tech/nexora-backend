package com.robotutor.nexora.common.messaging.services.impl

interface KafkaConfigurer {
    fun configureArgumentResolvers(configurer: KafkaArgumentResolverConfigurer): Unit
}
