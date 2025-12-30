package com.robotutor.nexora.common.messaging.resolver

import com.robotutor.nexora.common.resolver.ArgumentResolver
import org.springframework.stereotype.Component

@Component
class ArgumentResolverConfigurer {
    private val resolvers: MutableList<ArgumentResolver> = mutableListOf()

    fun getResolvers() = resolvers.toList()

    fun addCustomResolver(vararg resolver: ArgumentResolver) {
        resolvers.addAll(resolver)
        resolvers.distinct()
    }
}