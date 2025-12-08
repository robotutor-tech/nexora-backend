package com.robotutor.nexora.common.security.application.resolvers

import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Component
class WebFluxConfig(
    private val accountDataResolver: AccountDataResolver,
    private val actorDataResolver: ActorDataResolver,
    private val invitationDataResolver: InvitationDataResolver,
    private val deviceDataResolver: DeviceDataResolver,
) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(accountDataResolver)
        configurer.addCustomResolver(actorDataResolver)
        configurer.addCustomResolver(invitationDataResolver)
        configurer.addCustomResolver(deviceDataResolver)
    }
}