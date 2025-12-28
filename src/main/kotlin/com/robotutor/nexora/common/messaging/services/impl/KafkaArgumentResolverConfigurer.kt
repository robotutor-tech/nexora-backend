package com.robotutor.nexora.common.messaging.services.impl

import com.robotutor.nexora.common.messaging.resolver.EventKafkaArgumentResolver
import org.springframework.stereotype.Component

@Component
class KafkaArgumentResolverConfigurer(eventKafkaArgumentResolver: EventKafkaArgumentResolver) {
    val resolvers: MutableList<KafkaArgumentResolver> = mutableListOf(eventKafkaArgumentResolver)

    fun addCustomResolver(vararg resolver: KafkaArgumentResolver) {
        resolvers.addAll(resolver)
        resolvers.distinct()
    }
}