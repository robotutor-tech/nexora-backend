package com.robotutor.nexora.modules.premises.application.facade

import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
import com.robotutor.nexora.shared.domain.model.UserData
import reactor.core.publisher.Flux

interface ActorResourceFacade {
    fun getActors(userData: UserData): Flux<ActorWithRoles>
}
