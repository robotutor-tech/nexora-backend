package com.robotutor.nexora.modules.iam.application

import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.modules.iam.application.command.CreateActorCommand
import com.robotutor.nexora.modules.iam.domain.model.Actor
import com.robotutor.nexora.modules.iam.domain.model.IdType
import com.robotutor.nexora.modules.iam.domain.repository.ActorRepository
import com.robotutor.nexora.modules.iam.exceptions.NexoraError
import com.robotutor.nexora.shared.adapters.webclient.exceptions.DataNotFoundException
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ActorUseCase(
    private val idGeneratorService: IdGeneratorService,
    private val actorRepository: ActorRepository
) {
    val logger = Logger(this::class.java)

    fun createActor(createActorCommand: CreateActorCommand): Mono<Actor> {
        return idGeneratorService.generateId(IdType.ACTOR_ID)
            .map { actorId ->
                Actor(
                    actorId = ActorId(actorId),
                    premisesId = createActorCommand.premisesId,
                    principalType = createActorCommand.principalType,
                    principal = createActorCommand.principal,
                    roleIds = createActorCommand.roles,
                )
            }
            .flatMap { actor -> actorRepository.save(actor) }
            .logOnSuccess(logger, "Successfully created actor")
            .logOnError(logger, "", "Failed to create actor")
    }

    fun getActors(userData: UserData): Flux<Actor> {
        return actorRepository.findAllByPrincipalTypeAndPrincipal(ActorPrincipalType.USER, UserContext(userData.userId))
    }

    fun getActor(actorId: ActorId, roleId: RoleId): Mono<Actor> {
        return actorRepository.findByActorIdAndRoleId(actorId, roleId)
            .switchIfEmpty(createMonoError(DataNotFoundException(NexoraError.NEXORA0201)))
    }
}