package com.robotutor.nexora.modules.user.application.service

import com.robotutor.nexora.modules.user.application.command.RegisterAuthUserCommand
import reactor.core.publisher.Mono

interface RegisterAuthUser {
    fun register(registerAuthUserCommand: RegisterAuthUserCommand): Mono<RegisterAuthUserCommand>
}