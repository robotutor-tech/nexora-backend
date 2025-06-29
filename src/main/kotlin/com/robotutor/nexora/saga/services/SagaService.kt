package com.robotutor.nexora.saga.services

import com.robotutor.nexora.logger.ReactiveContext.getTraceId
import com.robotutor.nexora.saga.ReactiveSaga
import com.robotutor.nexora.saga.exceptions.NexoraError
import com.robotutor.nexora.saga.models.IdType
import com.robotutor.nexora.saga.models.Saga
import com.robotutor.nexora.saga.models.SagaStatus
import com.robotutor.nexora.saga.repositories.SagaRepository
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.webClient.exceptions.DataNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class SagaService(private val sagaRepository: SagaRepository, private val idGeneratorService: IdGeneratorService) {

    fun startSaga(name: String, metadata: Map<String, Any?>): Mono<ReactiveSaga> {
        return Mono.deferContextual { ctx ->
            val traceId = getTraceId(ctx)
            idGeneratorService.generateId(IdType.SAGA_ID)
                .map { sagaId ->
                    Saga(
                        sagaId = sagaId,
                        name = name,
                        status = SagaStatus.IN_PROGRESS,
                        metadata = metadata,
                        traceId = traceId
                    )
                }
                .flatMap {
                    sagaRepository.save(it)
                }
                .map { ReactiveSaga(it) }
        }
    }

    fun storeSaga(saga: Saga): Mono<Saga> {
        return sagaRepository.save(saga)
    }

    fun getSagaBySagaId(traceId: String): Mono<ReactiveSaga> {
        return sagaRepository.findBySagaId(traceId)
            .switchIfEmpty {
                createMonoError(DataNotFoundException(NexoraError.NEXORA0201))
            }
            .map { ReactiveSaga(it) }
    }
}
