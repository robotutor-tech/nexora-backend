package com.robotutor.nexora.shared.domain.policy

import reactor.core.publisher.Mono

interface Policy<T : Any> {
    fun evaluate(input: T): Mono<PolicyResult>
}
