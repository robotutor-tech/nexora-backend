package com.robotutor.nexora.auth.repositories

import com.robotutor.nexora.auth.models.AuthUser
import com.robotutor.nexora.security.models.UserId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AuthRepository : ReactiveCrudRepository<AuthUser, UserId> {
    fun existsByUserId(userId: UserId): Mono<Boolean>
    fun findByEmail(email: String): Mono<AuthUser>
    fun findByUserId(userId: UserId): Mono<AuthUser>
}
