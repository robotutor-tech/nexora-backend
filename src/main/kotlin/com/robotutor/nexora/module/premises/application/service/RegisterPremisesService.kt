package com.robotutor.nexora.module.premises.application.service

import com.robotutor.nexora.module.premises.application.command.RegisterPremisesCommand
import com.robotutor.nexora.module.premises.domain.aggregate.Premises
import com.robotutor.nexora.module.premises.domain.repository.PremisesIdGenerator
import com.robotutor.nexora.module.premises.domain.repository.PremisesRepository
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.domain.vo.ResourceType
import com.robotutor.nexora.shared.outbox.auditOnSuccess
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class RegisterPremisesService(
    private val premisesIdGenerator: PremisesIdGenerator,
    private val premisesRepository: PremisesRepository,
) {
    private val logger = Logger(this::class.java)

    @Transactional
    fun execute(command: RegisterPremisesCommand): Mono<Premises> {
        return premisesIdGenerator.generate()
            .map { premisesId ->
                Premises.register(
                    premisesId = premisesId,
                    name = command.name,
                    ownerId = command.owner.accountId,
                    address = command.address
                )
            }
            .flatMap { premises ->
                premisesRepository.save(premises)
                    .auditOnSuccess(
                        "PREMISES_REGISTERED",
                        ResourceType.PREMISES,
                        premises.premisesId,
                        command.toMetaData()
                    )
            }
            .logOnSuccess(logger = logger, message = "Successfully registered premises")
            .logOnError(logger, "Failed to register premises")
    }
}
