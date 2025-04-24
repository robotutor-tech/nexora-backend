package com.robotutor.nexora.premises.repositories

import com.robotutor.nexora.premises.models.Premises
import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.models.UserId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PremisesRepository : ReactiveCrudRepository<Premises, PremisesId> {
    fun findAllByCreatedBy(userId: UserId): Flux<Premises>

}
