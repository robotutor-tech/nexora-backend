package com.robotutor.nexora.shared.infrastructure.messaging.services.impl

import com.robotutor.nexora.shared.infrastructure.messaging.resolver.ActorDataKafkaArgumentResolver
import com.robotutor.nexora.shared.infrastructure.messaging.resolver.UserDataKafkaArgumentResolver
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfigurerImpl(
    private val actorDataKafkaArgumentResolver: ActorDataKafkaArgumentResolver,
    private val userDataKafkaArgumentResolver: UserDataKafkaArgumentResolver
) : KafkaConfigurer {
    override fun configureArgumentResolvers(configurer: KafkaArgumentResolverConfigurer) {
        configurer.addCustomResolver(actorDataKafkaArgumentResolver, userDataKafkaArgumentResolver)
    }
}