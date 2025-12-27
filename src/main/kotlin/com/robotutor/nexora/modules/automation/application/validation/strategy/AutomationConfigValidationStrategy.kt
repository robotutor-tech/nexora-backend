package com.robotutor.nexora.modules.automation.application.validation.strategy

import com.robotutor.nexora.shared.utility.createMonoError
import com.robotutor.nexora.modules.automation.domain.entity.config.AutomationConfig
import com.robotutor.nexora.modules.automation.domain.repository.AutomationRepository
import com.robotutor.nexora.modules.automation.domain.exception.NexoraError
import com.robotutor.nexora.shared.domain.exception.BadDataException
import com.robotutor.nexora.shared.domain.exception.ErrorResponse
import com.robotutor.nexora.shared.domain.vo.principal.ActorData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AutomationConfigValidationStrategy(private val automationRepository: AutomationRepository) :
    ValidationStrategy<AutomationConfig> {
    override fun validate(config: AutomationConfig, ActorData: ActorData): Mono<AutomationConfig> {
        val serviceError = ErrorResponse(
            NexoraError.NEXORA0301.errorCode, "Automation ID ${config.automationId} not found"
        )
        return automationRepository.findByAutomationIdAndPremisesId(config.automationId, ActorData.premisesId)
            .map { config }
            .switchIfEmpty(createMonoError(BadDataException(serviceError)))
    }
}