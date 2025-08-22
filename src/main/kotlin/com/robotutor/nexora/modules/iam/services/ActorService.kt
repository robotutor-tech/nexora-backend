package com.robotutor.nexora.modules.iam.services

import com.robotutor.nexora.shared.logger.Logger
import org.springframework.stereotype.Service

@Service
class ActorService(
) {
    val logger = Logger(this::class.java)

//    fun registerActor(request: RegisterActorRequest): Mono<ActorDocument> {
//        return idGeneratorService.generateId(IdType.ACTOR_ID)
//            .map { actorId -> ActorDocument.from(actorId, request) }
//            .flatMap { actor ->
//                actorRepository.save(actor)
//                    .auditOnSuccess(
//                        "ACTOR_CREATED",
//                        mapOf("actorId" to actor.actorId),
//                        request.identifier,
//                        actor.premisesId
//                    )
//            }
//            .logOnSuccess(logger, "Successfully created actor")
//            .logOnError(logger, "", "Failed to create actor")
//    }
//
//    fun getActor(actorId: ActorId, roleId: RoleId): Mono<ActorDocument> {
//        return actorRepository.findByActorIdAndRolesContaining(actorId, listOf(roleId))
//            .switchIfEmpty {
//                createMonoError(DataNotFoundException(NexoraError.NEXORA0201))
//            }
//    }
//
//    fun getActors(authUserData: AuthUserData): Flux<ActorDocument> {
//        return actorRepository.findAllByIdentifier_TypeAndIdentifier_Id(ActorIdentifier.USER, authUserData.userId)
//    }
}
