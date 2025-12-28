package com.robotutor.nexora.common.messaging.services.impl

import com.robotutor.nexora.common.messaging.resolver.ActorDataKafkaArgumentResolver
import com.robotutor.nexora.common.messaging.resolver.AccountDataKafkaArgumentResolver
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfigurerImpl(
    private val actorDataKafkaArgumentResolver: ActorDataKafkaArgumentResolver,
    private val accountDataKafkaArgumentResolver: AccountDataKafkaArgumentResolver
) : KafkaConfigurer {
    override fun configureArgumentResolvers(configurer: KafkaArgumentResolverConfigurer) {
        configurer.addCustomResolver(actorDataKafkaArgumentResolver, accountDataKafkaArgumentResolver)
    }
}