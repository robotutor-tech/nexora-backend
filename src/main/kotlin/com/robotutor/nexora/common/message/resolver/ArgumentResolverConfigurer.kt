package com.robotutor.nexora.common.message.resolver

import com.robotutor.nexora.common.resolver.AccountDataResolver
import com.robotutor.nexora.common.resolver.ActorDataResolver
import com.robotutor.nexora.common.resolver.ArgumentResolver
import com.robotutor.nexora.common.resolver.ResourceResolver
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

    fun addCustomResolver(vararg resolver: ArgumentResolver) {
        resolvers.plus(resolver).distinct()
    }
}