package com.robotutor.nexora.module.identity.application.facade

import com.robotutor.nexora.module.identity.application.command.RegisterUserAccountCommand
import com.robotutor.nexora.module.identity.domain.entity.User
import reactor.core.publisher.Mono

interface UserFacade {
    fun register(command: RegisterUserAccountCommand): Mono<User>
}
