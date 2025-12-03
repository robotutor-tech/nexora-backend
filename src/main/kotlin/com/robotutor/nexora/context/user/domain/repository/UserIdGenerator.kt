package com.robotutor.nexora.context.user.domain.repository

import com.robotutor.nexora.shared.domain.model.UserId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserIdGenerator {
    fun generate(): Mono<UserId>
}