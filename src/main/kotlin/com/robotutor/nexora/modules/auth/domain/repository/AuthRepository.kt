package com.robotutor.nexora.modules.auth.domain.repository

import com.robotutor.nexora.modules.auth.domain.model.AuthUser
import com.robotutor.nexora.shared.domain.model.Email
import reactor.core.publisher.Mono

interface AuthRepository {
    fun save(authUser: AuthUser): Mono<AuthUser>
    fun findByEmail(email: Email): Mono<AuthUser>
}