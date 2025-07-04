package com.robotutor.nexora.automation.services.validator

import com.robotutor.nexora.automation.controllers.views.TriggerRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.gateways.FeedGateway
import com.robotutor.nexora.automation.models.FeedTriggerConfig
import com.robotutor.nexora.automation.models.ScheduleConfig
import com.robotutor.nexora.automation.models.ScheduleTriggerConfig
import com.robotutor.nexora.automation.models.ScheduleType
import com.robotutor.nexora.automation.models.SunTriggerConfig
import com.robotutor.nexora.automation.models.TimeTriggerConfig
import com.robotutor.nexora.automation.models.TriggerConfig
import com.robotutor.nexora.automation.models.TriggerType
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
class TriggerValidator(private val triggerRepository: TriggerRepository, private val feedGateway: FeedGateway) {

    fun validateRequest(request: TriggerRequest, premisesActorData: PremisesActorData): Mono<Boolean> {
        val errorCode = NexoraError.NEXORA0307.errorCode
        return when (request.type) {
            TriggerType.SCHEDULE -> validateScheduleTriggerConfig(request.config, errorCode)
            TriggerType.VOICE -> validateVoiceTriggerConfig(request.config, errorCode, premisesActorData)
            TriggerType.FEED -> validateFeedTriggerConfig(request.config, errorCode)
        }
    }

    private fun validateFeedTriggerConfig(config: TriggerConfig, errorCode: String): Mono<Boolean> {
        if (config !is FeedTriggerConfig) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for FEED trigger type")))
        }
        return feedGateway.getFeedByFeedId(config.feedId)
            .map { true }
    }

    private fun validateVoiceTriggerConfig(
        config: TriggerConfig,
        errorCode: String,
        premisesActorData: PremisesActorData
    ): Mono<Boolean> {
        if (config !is VoiceTriggerConfig) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for VOICE trigger type")))
        }
        if (config.commands.isEmpty() || config.commands.any { it.isBlank() }) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Voice commands not found be empty or blank")
                )
            )
        }
        return triggerRepository.findByPremisesIdAndVoiceCommand(premisesActorData.premisesId, config.commands)
            .collectList()
            .map { triggers ->
                triggers.map { (it.config as VoiceTriggerConfig).commands }.flatten()
            }
            .flatMap { commands ->
                val conflicts = commands.filter { config.commands.contains(it) }
                if (conflicts.isNotEmpty()) {
                    createMonoError(
                        BadDataException(
                            ErrorResponse(errorCode, "Voice command(s) already used: ${conflicts.joinToString()}")
                        )
                    )
                } else {
                    createMono(true)
                }
            }
            .switchIfEmpty(createMono(true))

    }

    private fun validateScheduleTriggerConfig(config: TriggerConfig, errorCode: String): Mono<Boolean> {
        if (config !is ScheduleTriggerConfig) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for SCHEDULE trigger type")
                )
            )
        }

        if (config.repeat.isEmpty()) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Repeat days are required.")))
        }

        return when (config.type) {
            ScheduleType.TIME -> validateTimeTriggerConfig(config.config, errorCode)
            ScheduleType.SUN -> validateSunTriggerConfig(config.config, errorCode)
        }

    }

    private fun validateSunTriggerConfig(config: ScheduleConfig, errorCode: String): Mono<Boolean> {
        if (config !is SunTriggerConfig) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for SUN schedule type trigger.")
                )
            )
        }
        if (config.offsetMinutes < -60 || config.offsetMinutes > 60) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Offset minutes must be between -60 and 60 minutes.")
                )
            )
        }
        return createMono(true)
    }

    private fun validateTimeTriggerConfig(config: ScheduleConfig, errorCode: String): Mono<Boolean> {
        if (config !is TimeTriggerConfig) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for TIME schedule type trigger.")
                )
            )
        }
        if (!config.time.matches(Regex("^([01]\\d|2[0-3]):[0-5]\\d$"))) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid time format, expected HH:mm")))
        }
        return createMono(true)
    }
}