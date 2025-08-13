package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.modules.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.modules.iam.exceptions.NexoraError
import com.robotutor.nexora.modules.iam.models.Actor
import com.robotutor.nexora.modules.iam.models.IdType
import com.robotutor.nexora.modules.iam.models.RoleId
import com.robotutor.nexora.modules.iam.repositories.ActorRepository
import com.robotutor.nexora.shared.adapters.messaging.auditOnSuccess
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.common.security.models.ActorId
import com.robotutor.nexora.common.security.models.ActorIdentifier
import com.robotutor.nexora.common.security.models.AuthUserData
import com.robotutor.nexora.common.security.services.IdGeneratorService
import com.robotutor.nexora.shared.adapters.webclient.exceptions.DataNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class ActorService(
    private val idGeneratorService: IdGeneratorService,
    private val actorRepository: ActorRepository,
) {
    val logger = Logger(this::class.java)

    fun registerActor(request: RegisterActorRequest): Mono<Actor> {
        return idGeneratorService.generateId(IdType.ACTOR_ID)
            .map { actorId -> Actor.from(actorId, request) }
            .flatMap { actor ->
                actorRepository.save(actor)
                    .auditOnSuccess(
                        "ACTOR_CREATED",
                        mapOf("actorId" to actor.actorId),
                        request.identifier,
                        actor.premisesId
                    )
            }
            .logOnSuccess(logger, "Successfully created actor")
            .logOnError(logger, "", "Failed to create actor")
    }

    fun getActor(actorId: ActorId, roleId: RoleId): Mono<Actor> {
        return actorRepository.findByActorIdAndRolesContaining(actorId, listOf(roleId))
            .switchIfEmpty {
                createMonoError(DataNotFoundException(NexoraError.NEXORA0201))
            }
    }

    fun getActors(authUserData: AuthUserData): Flux<Actor> {
        return actorRepository.findAllByIdentifier_TypeAndIdentifier_Id(ActorIdentifier.USER, authUserData.userId)
    }
}
