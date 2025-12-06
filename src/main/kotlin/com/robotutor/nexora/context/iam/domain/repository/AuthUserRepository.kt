package com.robotutor.nexora.context.iam.domain.repository

import com.robotutor.nexora.context.iam.domain.entity.AuthUser
import com.robotutor.nexora.shared.domain.model.Email
import reactor.core.publisher.Mono

interface AuthUserRepository {
    fun save(authUser: AuthUser): Mono<AuthUser>
    fun findByEmail(email: Email): Mono<AuthUser>
}