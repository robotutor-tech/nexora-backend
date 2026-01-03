package com.robotutor.nexora.shared.application

import com.robotutor.nexora.shared.application.logger.ReactiveContext
import com.robotutor.nexora.shared.application.logger.TraceData
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.utility.createMono
import reactor.core.publisher.Mono

object ReactiveContext {
    fun getTraceData(): Mono<TraceData> {
        return Mono.deferContextual { context ->
            val premisesId = context.getOrDefault(ReactiveContext.X_PREMISES_ID, "missing-premisesId-id")!!
            val correlationId = context.getOrDefault(ReactiveContext.CORRELATION_ID, "missing-correlation-id")!!
            createMono(TraceData(correlationId, PremisesId(premisesId)))
        }
    }


    fun getPremisesId(): Mono<PremisesId> {
        return Mono.deferContextual { context ->
            val content = context.getOrDefault(ReactiveContext.X_PREMISES_ID, "missing-premisesId-id")!!
            createMono(PremisesId(content))
        }
    }

    fun getCorrelationId(): Mono<String> {
        return Mono.deferContextual { context ->
            val content = context.getOrDefault(ReactiveContext.CORRELATION_ID, "missing-correlation-id")!!
            createMono(content)
        }
    }
}