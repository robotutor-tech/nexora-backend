package com.robotutor.nexora.saga.repositories

import com.robotutor.nexora.saga.models.Saga
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface SagaRepository : ReactiveCrudRepository<Saga, String> {
    fun findBySagaId(sagaId: String): Mono<Saga>
}
