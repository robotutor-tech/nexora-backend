package com.robotutor.nexora.shared.infrastructure.messaging.services.impl

import com.robotutor.nexora.shared.infrastructure.messaging.resolver.EventKafkaArgumentResolver
import org.springframework.stereotype.Component

@Component
class KafkaArgumentResolverConfigurer(eventKafkaArgumentResolver: EventKafkaArgumentResolver) {
    val resolvers: MutableList<KafkaArgumentResolver> = mutableListOf(eventKafkaArgumentResolver)

    fun addCustomResolver(vararg resolver: KafkaArgumentResolver) {
        resolvers.addAll(resolver)
        resolvers.distinct()
    }
}