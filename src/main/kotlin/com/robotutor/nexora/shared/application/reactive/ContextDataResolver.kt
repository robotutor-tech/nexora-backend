package com.robotutor.nexora.shared.application.reactive

import com.robotutor.nexora.shared.application.logger.ReactiveContext.CORRELATION_ID
import com.robotutor.nexora.shared.application.logger.ReactiveContext.X_PREMISES_ID
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import reactor.core.publisher.Mono

object ContextDataResolver {

    fun getActorData(): Mono<ActorData> {
        return Mono.deferContextual { context ->
            val optional = context.getOrEmpty<ActorData>(ActorData::class.java)
            if (optional.isPresent) {
                createMono(optional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0101))
            }
        }
    }

    fun getAccountData(): Mono<AccountData> {
        return Mono.deferContextual { context ->
            val accountDataDataOptional = context.getOrEmpty<AccountData>(AccountData::class.java)
            if (accountDataDataOptional.isPresent) {
                createMono(accountDataDataOptional.get())
            } else {
                createMonoError(DataNotFoundException(SharedNexoraError.NEXORA0102))
            }
        }
    }

    fun getTraceData(): Mono<TraceData> {
        return Mono.deferContextual { context ->
            val premisesId = context.getOrDefault(X_PREMISES_ID, "missing-premisesId-id")
            val correlationId = context.getOrDefault(CORRELATION_ID, "missing-correlation-id")
            createMono(TraceData(correlationId!!, PremisesId(premisesId!!)))
        }
    }


    fun getPremisesId(): Mono<PremisesId> {
        return Mono.deferContextual { context ->
            val content = context.getOrDefault(X_PREMISES_ID, "missing-premisesId-id")
            createMono(PremisesId(content!!))
        }
    }

    fun getCorrelationId(): Mono<String> {
        return Mono.deferContextual { context ->
            val content = context.getOrDefault(CORRELATION_ID, "missing-correlation-id")
            createMono(content!!)
        }
    }
}