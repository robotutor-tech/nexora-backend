package com.robotutor.nexora.shared.resolver

import com.robotutor.nexora.shared.annotation.ResourceSelector
import com.robotutor.nexora.shared.resolver.client.IdentityClient
import com.robotutor.nexora.shared.domain.vo.Resources
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class ResourceResolver(private val IdentityClient: IdentityClient) : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == Resources::class.java &&
                parameter.isAnnotationPresent(ResourceSelector::class.java)
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        val resourceSelector = parameter.getAnnotation(ResourceSelector::class.java)
        return Mono.empty()
//        return IdentityClient.getResource(resourceSelector).flatMap { createMono(it) }
    }
}
