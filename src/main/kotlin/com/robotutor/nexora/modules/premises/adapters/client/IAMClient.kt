package com.robotutor.nexora.modules.premises.adapters.client

import com.robotutor.nexora.modules.iam.interfaces.controller.ActorController
import com.robotutor.nexora.modules.iam.interfaces.controller.PremisesController
import com.robotutor.nexora.modules.iam.interfaces.controller.dto.PremisesRequest
import com.robotutor.nexora.modules.premises.adapters.client.mapper.IAMMapper
import com.robotutor.nexora.modules.premises.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.modules.premises.application.facade.ActorResourceFacade
import com.robotutor.nexora.modules.premises.application.facade.PremisesResourceFacade
import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.domain.model.UserId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service("PremisesIAMClient")
class IAMClient(
    private val premisesController: PremisesController,
    private val actorController: ActorController
) : PremisesResourceFacade, ActorResourceFacade {
    override fun register(command: RegisterPremisesResourceCommand): Mono<ActorWithRoles> {
        return premisesController.registerPremises(
            request = PremisesRequest(premisesId = command.premisesId.value),
            userData = command.owner
        )
            .map { IAMMapper.toActorWithRoles(it) }
    }

    override fun getActors(userData: UserData): Flux<ActorWithRoles> {
        return actorController.getActors(userData)
            .map { IAMMapper.toActorWithRoles(it) }
    }
}