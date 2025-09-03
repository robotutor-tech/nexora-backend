package com.robotutor.nexora.modules.premises.application

import com.robotutor.nexora.modules.premises.application.command.CreatePremisesCommand
import com.robotutor.nexora.modules.premises.application.command.RegisterPremisesResourceCommand
import com.robotutor.nexora.modules.premises.application.dto.ActorWithRolesPremises
import com.robotutor.nexora.modules.premises.application.facade.ActorResourceFacade
import com.robotutor.nexora.modules.premises.application.facade.PremisesResourceFacade
import com.robotutor.nexora.modules.premises.domain.entity.IdType
import com.robotutor.nexora.modules.premises.domain.entity.Premises
import com.robotutor.nexora.modules.premises.domain.event.PremisesEvent
import com.robotutor.nexora.modules.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.publishEvents
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.logger.Logger
import com.robotutor.nexora.shared.logger.logOnError
import com.robotutor.nexora.shared.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PremisesUseCase(
    private val idGeneratorService: IdGeneratorService,
    private val premisesRepository: PremisesRepository,
    private val premisesResourceFacade: PremisesResourceFacade,
    private val actorResourceFacade: ActorResourceFacade,
    private val eventPublisher: EventPublisher<PremisesEvent>
) {
    val logger = Logger(this::class.java)

    fun createPremises(createPremisesCommand: CreatePremisesCommand): Mono<ActorWithRolesPremises> {
        return idGeneratorService.generateId(IdType.PREMISE_ID, PremisesId::class.java)
            .map { premisesId ->
                Premises.register(
                    premisesId = premisesId,
                    name = createPremisesCommand.name,
                    owner = createPremisesCommand.owner.userId,
                    address = createPremisesCommand.address,
                )
            }
            .flatMap { premises -> premisesRepository.save(premises).map { premises } }
            .publishEvents(eventPublisher)
            .flatMap { premises ->
                val command = RegisterPremisesResourceCommand(premises.premisesId, createPremisesCommand.owner)
                premisesResourceFacade.register(command)
                    .map { actor -> ActorWithRolesPremises(actor, premises) }
            }
            .logOnSuccess(logger, "Successfully created premise")
            .logOnError(logger, "", "Failed to create premise")
    }

    fun getAllPremises(userData: UserData): Flux<ActorWithRolesPremises> {
        return actorResourceFacade.getActors(userData).collectList()
            .flatMapMany { actor ->
                println("actors: $actor-----------------")
                val premisesIds = actor.map { it.premisesId }.distinct()
                premisesRepository.findAllByPremisesIdIn(premisesIds)
                    .map { premises ->
                        ActorWithRolesPremises(
                            premises = premises,
                            actor = actor.find { it.premisesId == premises.premisesId }!!
                        )
                    }
            }
    }

    fun getPremisesDetails(premisesId: PremisesId): Mono<Premises> {
        return premisesRepository.findByPremisesId(premisesId)
    }
}