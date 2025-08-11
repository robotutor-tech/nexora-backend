package com.robotutor.nexora.common.security.filters

import com.robotutor.nexora.common.security.filters.resolvers.InvitationDataResolver
import com.robotutor.nexora.common.security.filters.resolvers.UserDataResolver
import com.robotutor.nexora.common.security.filters.resolvers.PremisesActorDataResolver
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val userDataResolver: UserDataResolver,
    private val premisesActorDataResolver: PremisesActorDataResolver,
    private val invitationDataResolver: InvitationDataResolver,
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(userDataResolver)
        configurer.addCustomResolver(premisesActorDataResolver)
        configurer.addCustomResolver(invitationDataResolver)
    }
}
