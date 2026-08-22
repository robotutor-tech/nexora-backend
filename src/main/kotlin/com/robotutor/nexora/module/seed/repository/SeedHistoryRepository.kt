package com.robotutor.nexora.module.seed.repository

import com.robotutor.nexora.module.seed.document.SeedHistory
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface SeedHistoryRepository : ReactiveCrudRepository<SeedHistory, String> {
    fun findByName(name: String): Mono<SeedHistory>
}
