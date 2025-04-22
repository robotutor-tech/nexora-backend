package com.robotutor.nexora.security.filters

import com.robotutor.nexora.security.filters.resolvers.UserDataResolver
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val userDataResolver: UserDataResolver,
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(userDataResolver)
    }
}
