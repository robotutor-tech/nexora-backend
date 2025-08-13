package com.robotutor.nexora.modules.automation.services.validator

import com.robotutor.nexora.modules.automation.exceptions.NexoraError
import com.robotutor.nexora.modules.automation.gateways.FeedGateway
import com.robotutor.nexora.modules.automation.models.ConditionConfig
import com.robotutor.nexora.modules.automation.models.FeedConditionConfig
import com.robotutor.nexora.modules.automation.models.TimeRangeConditionConfig
import com.robotutor.nexora.common.security.createMono
import com.robotutor.nexora.common.security.createMonoError
import com.robotutor.nexora.shared.adapters.webclient.exceptions.BadDataException
import com.robotutor.nexora.shared.adapters.webclient.exceptions.ErrorResponse
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