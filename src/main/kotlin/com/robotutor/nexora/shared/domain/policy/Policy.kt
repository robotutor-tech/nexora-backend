package com.robotutor.nexora.shared.domain.policy

import com.robotutor.nexora.shared.application.command.Command
import reactor.core.publisher.Mono

interface Policy<T : Command> {
    fun evaluate(command: T): Mono<PolicyResult>
    fun getName(): String
}
