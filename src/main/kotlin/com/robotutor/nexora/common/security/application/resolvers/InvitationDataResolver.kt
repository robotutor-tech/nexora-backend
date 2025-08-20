package com.robotutor.nexora.common.security.application.resolvers

import com.robotutor.nexora.shared.adapters.webclient.exceptions.UnAuthorizedException
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.domain.exceptions.NexoraError
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import com.robotutor.nexora.common.security.models.InvitationData
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class InvitationDataResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == InvitationData::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return Mono.deferContextual { context ->
            val invitationData = context.getOrEmpty<InvitationData>(InvitationData::class.java)
            if (invitationData.isPresent) {
                createMono(invitationData.get())
            } else {
                createMonoError(UnAuthorizedException(NexoraError.NEXORA0104))
            }
        }
    }
}
