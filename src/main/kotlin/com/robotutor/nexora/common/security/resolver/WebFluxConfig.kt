package com.robotutor.nexora.common.security.resolver

import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val accountDataResolver: HandlerAccountDataResolver,
    private val actorDataResolver: HandlerActorDataResolver,
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(accountDataResolver, actorDataResolver)
    }
}