package com.robotutor.nexora.shared.resolver

import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val resolvers: List<ArgumentResolver>,
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(
            *resolvers.map { HandlerMethodResolver.from(it) }.toTypedArray()
        )
    }
}
