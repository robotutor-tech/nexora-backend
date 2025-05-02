package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.iam.controllers.view.RegisterActorsRequest
import com.robotutor.nexora.iam.exceptions.NexoraError
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.repositories.ActorRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.models.InvitationData
import com.robotutor.nexora.security.services.IdGeneratorService
import com.robotutor.nexora.webClient.exceptions.DataNotFoundException
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

    fun registerActors(request: RegisterActorsRequest, authUserData: AuthUserData): Flux<Actor> {
        return createFlux(request.roles)
            .flatMap { roleId ->
                idGeneratorService.generateId(IdType.ACTOR_ID)
                    .map { actorId ->
                        Actor.from(actorId, request.premisesId, authUserData.userId, ActorIdentifier.HUMAN, roleId)
                    }
            }
            .flatMap { actor -> actorRepository.save(actor) }
            .logOnSuccess(logger, "Successfully created actor")
            .logOnError(logger, "", "Failed to create actor")
    }

    fun registerActor(request: RegisterActorRequest, invitationData: InvitationData): Mono<Actor> {
        return idGeneratorService.generateId(IdType.ACTOR_ID)
            .map { actorId ->
                Actor.from(
                    actorId,
                    invitationData.premisesId,
                    request.identifier,
                    request.type,
                    request.role
                )
            }
            .flatMap { actor -> actorRepository.save(actor) }
            .logOnSuccess(logger, "Successfully created actor")
            .logOnError(logger, "", "Failed to create actor")
    }

    fun getActor(actorId: ActorId): Mono<Actor> {
        return actorRepository.findByActorId(actorId)
            .switchIfEmpty {
                createMonoError(DataNotFoundException(NexoraError.NEXORA0201))
            }
    }

    fun getActors(authUserData: AuthUserData): Flux<Actor> {
        return actorRepository.findAllByActorIdentifier_TypeAndActorIdentifier_Id(
            ActorIdentifier.HUMAN, authUserData.userId
        )
    }

}
