package com.robotutor.nexora.automation.services.validator

import com.robotutor.nexora.automation.controllers.views.ScheduleTriggerRequest
import com.robotutor.nexora.automation.controllers.views.TriggerRequest
import com.robotutor.nexora.automation.exceptions.NexoraError
import com.robotutor.nexora.automation.gateways.FeedGateway
import com.robotutor.nexora.automation.models.FeedTriggerConfig
import com.robotutor.nexora.automation.models.ScheduleTriggerConfig
import com.robotutor.nexora.automation.models.ScheduleType
import com.robotutor.nexora.automation.models.SunTriggerConfig
import com.robotutor.nexora.automation.models.TimeTriggerConfig
import com.robotutor.nexora.automation.models.TriggerConfig
import com.robotutor.nexora.automation.models.TriggerType
import com.robotutor.nexora.automation.models.VoiceTriggerConfig
import com.robotutor.nexora.automation.repositories.TriggerRepository
import com.robotutor.nexora.logger.serializer.DefaultSerializer
import com.robotutor.nexora.security.createMono
import com.robotutor.nexora.security.createMonoError
import com.robotutor.nexora.security.models.PremisesActorData
import com.robotutor.nexora.webClient.exceptions.BadDataException
import com.robotutor.nexora.webClient.exceptions.ErrorResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TriggerValidator(private val triggerRepository: TriggerRepository, private val feedGateway: FeedGateway) {

    fun validateRequest(request: TriggerRequest, premisesActorData: PremisesActorData): Mono<TriggerConfig> {
        val errorCode = NexoraError.NEXORA0307.errorCode
        val config = DefaultSerializer.serialize(request.config)
        return when (request.type) {
            TriggerType.SCHEDULE -> validateScheduleTriggerConfig(config, errorCode)
            TriggerType.VOICE -> validateVoiceTriggerConfig(config, errorCode, premisesActorData)
            TriggerType.FEED -> validateFeedTriggerConfig(config, errorCode)
        } as Mono<TriggerConfig>
    }

    private fun validateFeedTriggerConfig(config: String, errorCode: String): Mono<FeedTriggerConfig> {
        try {
            val feedTriggerConfig = DefaultSerializer.deserialize(config, FeedTriggerConfig::class.java)
            return feedGateway.getFeedByFeedId(feedTriggerConfig.feedId)
                .map { feedTriggerConfig }
        } catch (_: Exception) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for FEED trigger type")))
        }
    }

    private fun validateVoiceTriggerConfig(
        config: String,
        errorCode: String,
        premisesActorData: PremisesActorData
    ): Mono<VoiceTriggerConfig> {
        try {
            val voiceTriggerConfig = DefaultSerializer.deserialize(config, VoiceTriggerConfig::class.java)
            voiceTriggerConfig.sanitizeCommands()
            if (voiceTriggerConfig.commands.isEmpty() || voiceTriggerConfig.commands.any { it.isBlank() }) {
                return createMonoError(
                    BadDataException(
                        ErrorResponse(errorCode, "Voice commands should not be empty or blank")
                    )
                )
            }
            return triggerRepository.findAllByPremisesIdAndVoiceCommands(
                premisesActorData.premisesId,
                voiceTriggerConfig.commands.map { command ->
                    Regex("^$command$", RegexOption.IGNORE_CASE)
                }.joinToString("|")
            )
                .collectList()
                .map { triggers ->
                    triggers.map { (it.config as VoiceTriggerConfig).commands }.flatten()
                }
                .flatMap { commands ->
                    val conflicts = commands.filter { voiceTriggerConfig.commands.contains(it) }
                    if (conflicts.isNotEmpty()) {
                        createMonoError(
                            BadDataException(
                                ErrorResponse(errorCode, "Voice command(s) already used: ${conflicts.joinToString()}")
                            )
                        )
                    } else {
                        createMono(voiceTriggerConfig)
                    }
                }
                .switchIfEmpty(createMono(voiceTriggerConfig))
                .map {
                    it.commands.map { command ->
                        command.split(' ').filter { command -> command != "" }.joinToString(" ")
                    }
                    it
                }
        } catch (_: Exception) {
            return createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid config for VOICE trigger type")))
        }
    }

    private fun validateScheduleTriggerConfig(
        config: String,
        errorCode: String
    ): Mono<ScheduleTriggerConfig> {
        try {
            val scheduleTriggerRequest = DefaultSerializer.deserialize(config, ScheduleTriggerRequest::class.java)
            if (scheduleTriggerRequest.repeat.isEmpty()) {
                return createMonoError(BadDataException(ErrorResponse(errorCode, "Repeat days are required.")))
            }
            val scheduleConfig = scheduleTriggerRequest.config.toString()
            return when (scheduleTriggerRequest.type) {
                ScheduleType.TIME -> validateTimeTriggerConfig(scheduleConfig, errorCode)
                ScheduleType.SUN -> validateSunTriggerConfig(scheduleConfig, errorCode)
            }
                .map {
                    ScheduleTriggerConfig(
                        type = scheduleTriggerRequest.type,
                        config = it,
                        repeat = scheduleTriggerRequest.repeat.sorted()
                    )
                }
        } catch (_: Exception) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for SCHEDULE trigger type")
                )
            )
        }


    }

    private fun validateSunTriggerConfig(config: String, errorCode: String): Mono<SunTriggerConfig> {
        try {
            val sunTriggerConfig = DefaultSerializer.deserialize(config, SunTriggerConfig::class.java)
            if (sunTriggerConfig.offsetMinutes < -60 || sunTriggerConfig.offsetMinutes > 60) {
                return createMonoError(
                    BadDataException(
                        ErrorResponse(errorCode, "Offset minutes must be between -60 and 60 minutes.")
                    )
                )
            }
            return createMono(sunTriggerConfig)
        } catch (e: Exception) {
            e.printStackTrace()
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for SUN schedule type trigger.")
                )
            )
        }

    }

    private fun validateTimeTriggerConfig(config: String, errorCode: String): Mono<TimeTriggerConfig> {
        try {
            val timeTriggerConfig = DefaultSerializer.deserialize(config, TimeTriggerConfig::class.java)
            if (!timeTriggerConfig.time.matches(Regex("^([01]\\d|2[0-3]):[0-5]\\d$"))) {
                return createMonoError(
                    BadDataException(
                        ErrorResponse(errorCode, "Invalid time format, expected HH:mm")
                    )
                )
            }
            return createMono(timeTriggerConfig)
        } catch (_: Exception) {
            return createMonoError(
                BadDataException(
                    ErrorResponse(errorCode, "Invalid config for TIME schedule type trigger.")
                )
            )
        }
    }
}