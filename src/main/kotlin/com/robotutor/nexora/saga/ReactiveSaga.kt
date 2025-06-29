package com.robotutor.nexora.saga

import com.robotutor.nexora.saga.models.Saga
import com.robotutor.nexora.saga.models.SagaStatus
import com.robotutor.nexora.saga.models.SagaStep
import com.robotutor.nexora.saga.models.StepStatus
import com.robotutor.nexora.saga.services.SagaService
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.webClient.exceptions.ServerException
import com.robotutor.nexora.webClient.exceptions.ServiceError
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

class ReactiveSaga(val saga: Saga) {
    private val compensations = mutableListOf<() -> Mono<Void>>()

    fun addCompensation(stepName: String, resource: Map<String, Any?>?, comp: () -> Mono<Void>) {
        val compensation = {
            this.addStep(stepName, resource)
            comp()
        }
        compensations.add(compensation)
    }

    fun compensate(throwable: Throwable): Mono<Void> {
        this.failStep(saga.steps.last().name, throwable.message)
        return Flux.fromIterable(compensations.reversed())
            .concatMap { it() }
            .then()
    }

    fun addStep(stepName: String, resource: Map<String, Any?>?): ReactiveSaga {
        saga.steps.add(SagaStep(name = stepName, status = StepStatus.IN_PROGRESS, resource = resource))
        saga.updatedAt = Instant.now()
        return this
    }

    fun completeStep(stepName: String): ReactiveSaga {
        val step = saga.steps.find { it.name == stepName }
        step?.apply {
            status = StepStatus.COMPLETED
            endedAt = Instant.now()
        }
        saga.updatedAt = Instant.now()
        return this
    }

    fun failStep(stepName: String, error: String?): ReactiveSaga {
        val step = saga.steps.find { it.name == stepName }
        step?.apply {
            status = StepStatus.FAILED
            this.error = error
            endedAt = Instant.now()
        }
        saga.status = SagaStatus.FAILED
        saga.updatedAt = Instant.now()
        return this
    }

    fun compensateSaga(): ReactiveSaga {
        this.saga.status = SagaStatus.COMPENSATED
        return this
    }
}

fun <T, R> Mono<T>.addCompensate(
    saga: ReactiveSaga,
    stepName: String,
    resource: (T) -> Map<String, Any?>?,
    compensation: (T) -> Mono<R>
): Mono<T> {
    return this.doOnNext { result ->
        saga.addCompensation(stepName, resource(result)) { compensation(result).then() }
    }
}

fun <T> Mono<T>.compensate(
    sagaService: SagaService,
    saga: ReactiveSaga,
    error: ServiceError,
): Mono<T> {
    return this
        .map { result ->
            saga.saga.status = SagaStatus.COMPLETED
            saga.saga.updatedAt = Instant.now()
            result
        }
        .onErrorResume { throwable ->
            saga.compensate(throwable).then(sagaService.storeSaga(saga.saga))
                .flatMap { createMonoError(ServerException(error)) }
        }
        .flatMap { result ->
            sagaService.storeSaga(saga.saga).map { result }
        }
}

