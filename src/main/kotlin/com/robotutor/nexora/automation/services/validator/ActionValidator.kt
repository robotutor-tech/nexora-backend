package com.robotutor.nexora.automation.services.validator

import com.robotutor.nexora.automation.controllers.views.ActionRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.gateways.FeedGateway
import com.robotutor.nexora.automation.models.*
import com.robotutor.nexora.automation.repositories.AutomationRepository
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ActionValidator(
    private val feedGateway: FeedGateway,
    private val automationRepository: AutomationRepository
) {

    fun validateRequest(request: ActionRequest, premisesActorData: PremisesActorData): Mono<Boolean> {
        val errorCode = NexoraError.NEXORA0308.errorCode
        return when (request.type) {
            ActionType.DELAY -> validateDelayActionConfig(request.config, errorCode)
            ActionType.FEED_CONTROL -> validateFeedActionConfig(request.config, errorCode)
            ActionType.AUTOMATION_TRIGGER -> validateAutomationConfig(request.config, errorCode, premisesActorData)
        }
    }

    private fun validateAutomationConfig(
        config: ActionConfig,
        errorCode: String,
        premisesActorData: PremisesActorData
    ): Mono<Boolean> {
        if (config !is AutomationActionConfig) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for AUTOMATION action type")
                )
            )
        }
        return automationRepository.findByAutomationIdAndPremisesId(config.automationId, premisesActorData.premisesId)
            .map { true }
            .switchIfEmpty(
                createMonoError(
                    BadDataException(
                        ErrorResponse(errorCode, "Automation ID ${config.automationId} not found")
                    )
                )
            )
    }

    private fun validateDelayActionConfig(config: ActionConfig, errorCode: String): Mono<Boolean> {
        if (config !is DelayActionConfig) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for DELAY action type")))
        }
        if (config.durationInMinute > 0 && config.durationInMinute < 120) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Duration must be between 0 and 120")))
        }
        return createMono(true)
    }

    private fun validateFeedActionConfig(
        config: ActionConfig,
        errorCode: String,
    ): Mono<Boolean> {
        if (config !is FeedControlActionConfig) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for FEED_CONTROL action type")
                )
            )
        }
        return feedGateway.getFeedByFeedId(config.feedId)
            .map { true }
    }
}