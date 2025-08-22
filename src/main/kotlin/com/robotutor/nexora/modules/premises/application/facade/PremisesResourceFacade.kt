package com.robotutor.nexora.modules.premises.application.facade

import com.robotutor.nexora.modules.premises.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
import reactor.core.publisher.Mono

interface PremisesResourceFacade {
    fun register(command: RegisterPremisesResourceCommand): Mono<ActorWithRoles>
}
