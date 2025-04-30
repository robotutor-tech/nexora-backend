package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.ActorId
import com.robotutor.nexora.iam.models.ActorType
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.repositories.ActorRepository
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createFlux
import com.robotutor.nexora.security.models.AuthUserData
import com.robotutor.nexora.security.services.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ActorService(
    private val idGeneratorService: IdGeneratorService,
    private val actorRepository: ActorRepository,
    private val roleService: RoleService
) {
    val logger = Logger(this::class.java)

    fun registerActor(request: RegisterActorRequest): Flux<Actor> {
        return createFlux(request.roles)
            .flatMap { roleId ->
                idGeneratorService.generateId(IdType.ACTOR_ID)
                    .map { actorId -> Actor.from(actorId, request, roleId) }
            }
            .flatMap { actor -> actorRepository.save(actor) }
            .logOnSuccess(logger, "Successfully created actor")
            .logOnError(logger, "", "Failed to create actor")
    }

    fun getActor(actorId: ActorId): Mono<Actor> {
        return actorRepository.findByActorId(actorId)
    }

    fun getActors(authUserData: AuthUserData): Flux<Actor> {
        return actorRepository.findAllByTypeAndIdentifier(ActorType.HUMAN, authUserData.userId)
    }

}
