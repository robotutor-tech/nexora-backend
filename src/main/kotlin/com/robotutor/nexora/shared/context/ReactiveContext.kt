package com.robotutor.nexora.shared.context

import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.message.message.MessageContext
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

object ReactiveContext {
    const val CORRELATION_ID = "correlation-id"
    const val MESSAGE_CONTEXT = "message-context"
    const val PRINCIPAL_DATA = "principal-data"
    const val EVENT_MESSAGE = "event-message"

    fun getPrincipalData(): Mono<PrincipalData> {
        return ReactiveSecurityContextHolder.getContext()
            .map { it.authentication.principal as PrincipalData }
    }

    fun getCorrelationId(contextView: ContextView): String {
        return contextView.getOrDefault<String>(CORRELATION_ID, "missing-correlation-id")!!
    }

    fun getCorrelationId(): Mono<String> {
        return Mono.deferContextual {
            createMono(getCorrelationId(it))
        }
    }

    fun getContextData(): Mono<ContextData> {
        return getCorrelationId()
            .flatMap { correlationId ->
                getPrincipalData()
                    .map { principalData -> ContextData(correlationId, principalData) }
                    .switchIfEmpty(createMono(ContextData(correlationId, null)))
            }
    }

    fun getMessageContext(): Mono<MessageContext> {
        return Mono.deferContextual {
            createMono(it.getOrDefault<MessageContext>(MESSAGE_CONTEXT, null))
        }
    }
}
