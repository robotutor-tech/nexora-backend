package com.robotutor.nexora.common.messaging.services

import com.robotutor.nexora.common.messaging.resolver.ArgumentResolverConfigurer
import com.robotutor.nexora.common.resolver.AccountDataResolver
import com.robotutor.nexora.common.resolver.ActorDataResolver
import org.springframework.context.annotation.Configuration

@Configuration
class ArgumentConfigurer(
    private val actorDataResolver: ActorDataResolver,
    private val accountDataResolver: AccountDataResolver
) {
    fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(actorDataResolver, accountDataResolver)
    }
}