package com.robotutor.nexora.shared.message.resolver

import com.robotutor.nexora.shared.resolver.AccountDataResolver
import com.robotutor.nexora.shared.resolver.ActorDataResolver
import com.robotutor.nexora.shared.resolver.ArgumentResolver
import com.robotutor.nexora.shared.resolver.ResourceResolver
import org.springframework.stereotype.Component

@Component
class ArgumentResolverConfigurer(
    actorDataResolver: ActorDataResolver,
    accountDataResolver: AccountDataResolver,
    resourceResolver: ResourceResolver,
    eventMessageResolver: EventMessageResolver,
) {
    private val resolvers: MutableList<ArgumentResolver> = mutableListOf(
        actorDataResolver,
        accountDataResolver,
        resourceResolver,
        eventMessageResolver
    )

    fun getResolvers() = resolvers.toList()
}
