package com.robotutor.nexora.automation.services.validator

import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.gateways.FeedGateway
import com.robotutor.nexora.automation.models.ConditionConfig
import com.robotutor.nexora.automation.models.FeedConditionConfig
import com.robotutor.nexora.automation.models.TimeRangeConditionConfig
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ConditionValidator(private val feedGateway: FeedGateway) {

    fun validateRequest(config: ConditionConfig): Mono<ConditionConfig> {
        val errorCode = NexoraError.NEXORA0309.errorCode
        return when (config) {
            is TimeRangeConditionConfig -> validateTimeRangeConditionConfig(config, errorCode)
            is FeedConditionConfig -> validateFeedConditionConfig(config)
        }
            .map { it as ConditionConfig }
    }

    private fun validateFeedConditionConfig(config: FeedConditionConfig): Mono<FeedConditionConfig> {
        return feedGateway.getFeedByFeedId(config.feedId).map { config }
    }

    private fun validateTimeRangeConditionConfig(
        config: TimeRangeConditionConfig,
        errorCode: String,
    ): Mono<TimeRangeConditionConfig> {
        val regex = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")
        if (!config.startTime.matches(regex) || !config.endTime.matches(regex)) {
            return createMonoError(
                BadDataException(ErrorResponse(errorCode, "Invalid time format, expected HH:mm"))
            )
        }
        if (config.startTime == config.endTime) {
            return createMonoError(
                BadDataException(ErrorResponse(errorCode, "Start time and end time should not be same"))
            )
        }
        return createMono(config)
    }

}