package com.robotutor.nexora.automation.services.validator

import com.robotutor.nexora.automation.controllers.views.ConditionRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.gateways.FeedGateway
import com.robotutor.nexora.automation.models.ConditionConfig
import com.robotutor.nexora.automation.models.ConditionType
import com.robotutor.nexora.automation.models.FeedConditionConfig
import com.robotutor.nexora.automation.models.ScheduleConfig
import com.robotutor.nexora.automation.models.ScheduleTriggerConfig
import com.robotutor.nexora.automation.models.ScheduleType
import com.robotutor.nexora.automation.models.SunTriggerConfig
import com.robotutor.nexora.automation.models.TimeRangeConditionConfig
import com.robotutor.nexora.automation.models.TimeTriggerConfig
import com.robotutor.nexora.automation.models.TriggerConfig
import com.robotutor.nexora.automation.models.VoiceTriggerConfig
import com.robotutor.nexora.automation.repositories.TriggerRepository
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ConditionValidator(private val feedGateway: FeedGateway) {

    fun validateRequest(request: ConditionRequest, premisesActorData: PremisesActorData): Mono<Boolean> {
        val errorCode = NexoraError.NEXORA0309.errorCode
        return when (request.type) {
            ConditionType.TIME_RANGE -> validateTimeRangeConditionConfig(request.config, errorCode)
            ConditionType.FEED -> validateFeedConditionConfig(request.config, errorCode)
        }
    }

    private fun validateFeedConditionConfig(config: ConditionConfig, errorCode: String): Mono<Boolean> {
        if (config !is FeedConditionConfig) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for FEED condition type")))
        }
        return feedGateway.getFeedByFeedId(config.feedId)
            .map { true }
    }

    private fun validateTimeRangeConditionConfig(
        config: ConditionConfig,
        errorCode: String,
    ): Mono<Boolean> {
        if (config !is TimeRangeConditionConfig) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for TIME RANGE trigger type")
                )
            )
        }
        if (!config.startTime.matches(Regex("^([01]\\d|2[0-3]):[0-5]\\d$")) || !config.endTime.matches(Regex("^([01]\\d|2[0-3]):[0-5]\\d$"))) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid time format, expected HH:mm")))
        }
        return createMono(true)
    }

}