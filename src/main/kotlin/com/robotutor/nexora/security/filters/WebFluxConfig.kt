package com.robotutor.nexora.security.filters

import com.robotutor.nexora.security.filters.resolvers.UserDataResolver
import com.robotutor.nexora.security.filters.resolvers.UserPremisesDataResolver
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val userDataResolver: UserDataResolver,
    private val userPremisesDataResolver: UserPremisesDataResolver,
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(userDataResolver)
        configurer.addCustomResolver(userPremisesDataResolver)
    }
}
