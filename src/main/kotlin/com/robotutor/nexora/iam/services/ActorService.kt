package com.robotutor.nexora.iam.services

import com.robotutor.nexora.iam.controllers.view.ActorWithRoleView
import com.robotutor.nexora.iam.controllers.view.RegisterActorRequest
import com.robotutor.nexora.iam.exceptions.NexoraError
import com.robotutor.nexora.iam.models.Actor
import com.robotutor.nexora.iam.models.IdType
import com.robotutor.nexora.iam.models.Role
import com.robotutor.nexora.iam.models.RoleId
import com.robotutor.nexora.iam.repositories.ActorRepository
import com.robotutor.nexora.kafka.auditOnSuccess
import com.robotutor.nexora.logger.Logger
import com.robotutor.nexora.logger.logOnError
import com.robotutor.nexora.logger.logOnSuccess
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.filters.ResourceEntitlement
import com.robotutor.nexora.security.models.ActorId
import com.robotutor.nexora.security.models.ActorIdentifier
import com.robotutor.nexora.security.models.AuthUserData
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
    private val roleService: RoleService,
    private val roleEntitlementService: RoleEntitlementService,
    private val entitlementService: EntitlementService,
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

    fun getActor(actorId: ActorId, roleId: RoleId): Mono<ActorWithRoleView> {
        return actorRepository.findByActorId(actorId)
            .switchIfEmpty {
                createMonoError(DataNotFoundException(NexoraError.NEXORA0201))
            }
            .flatMap { actor ->
                roleService.getRoleByRoleId(roleId)
                    .flatMap { role ->
                        getEntitlement(role)
                            .collectList()
                            .map { ActorWithRoleView.from(actor, role, it) }
                    }
            }
    }

    private fun getEntitlement(role: Role): Flux<ResourceEntitlement> {
        return roleEntitlementService.getAllByRoleId(role.roleId)
            .flatMap { entitlement ->
                entitlementService.getByEntitlementId(entitlement.entitlementId)
                    .map {
                        ResourceEntitlement(
                            action = it.action,
                            resourceType = it.resourceType,
                            resourceId = entitlement.resourceId,
                            premisesId = entitlement.premisesId,
                        )
                    }
            }
    }

    fun getActors(authUserData: AuthUserData): Flux<Actor> {
        return actorRepository.findAllByIdentifier_TypeAndIdentifier_Id(ActorIdentifier.USER, authUserData.userId)
    }
}
