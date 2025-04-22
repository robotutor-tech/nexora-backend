package com.robotutor.nexora.user.repositories

import com.robotutor.nexora.user.models.UserDetails
import com.robotutor.nexora.utils.models.UserId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<UserDetails, UserId> {
    fun existsByEmail(email: String): Mono<Boolean>
}
