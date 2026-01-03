package com.robotutor.nexora.common.resolver

import com.robotutor.nexora.common.resolver.client.IamClient
import com.robotutor.nexora.common.resource.annotation.ResourceSelector
import com.robotutor.nexora.shared.domain.vo.Resources
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class ResourceResolver(private val iamClient: IamClient) : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == Resources::class.java &&
                parameter.isAnnotationPresent(ResourceSelector::class.java)
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        val resourceSelector = parameter.getAnnotation(ResourceSelector::class.java)
        return iamClient.getResource(resourceSelector).flatMap { createMono(it) }
    }
}
