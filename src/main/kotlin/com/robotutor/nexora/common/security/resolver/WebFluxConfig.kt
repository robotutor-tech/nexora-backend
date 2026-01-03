package com.robotutor.nexora.common.security.resolver

import com.robotutor.nexora.common.resolver.AccountDataResolver
import com.robotutor.nexora.common.resolver.ActorDataResolver
import com.robotutor.nexora.common.resolver.ResourceResolver
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val handlerResolver: HandlerResolver,
    private val accountDataResolver: AccountDataResolver,
    private val actorDataResolver: ActorDataResolver,
    private val resourceResolver: ResourceResolver
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(
            handlerResolver.from(accountDataResolver),
            handlerResolver.from(actorDataResolver),
            handlerResolver.from(resourceResolver)
        )
    }
}

