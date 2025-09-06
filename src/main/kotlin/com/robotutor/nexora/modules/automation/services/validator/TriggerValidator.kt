//package com.robotutor.nexora.modules.automation.services.validator
//
//import com.robotutor.nexora.modules.automation.exceptions.NexoraError
//import com.robotutor.nexora.modules.automation.gateways.FeedGateway
//import com.robotutor.nexora.modules.automation.models.*
//import com.robotutor.nexora.modules.automation.repositories.TriggerRepository
//import com.robotutor.nexora.modules.automation.services.converter.TriggerConverter
//import com.robotutor.nexora.common.security.createMono
//import com.robotutor.nexora.common.security.createMonoError
//import com.robotutor.nexora.common.security.models.PremisesActorData
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.BadDataException
//import com.robotutor.nexora.shared.adapters.webclient.exceptions.ErrorResponse
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class TriggerValidator(
//    private val triggerRepository: TriggerRepository,
//    private val feedGateway: FeedGateway,
//    private val triggerConverter: TriggerConverter
//) {
//
//    fun validateTriggers(triggerIds: List<TriggerId>, premisesActorData: PremisesActorData): Mono<List<TriggerId>> {
//        if (triggerIds.isEmpty())
//            return createMonoError(BadDataException(NexoraError.NEXORA0301))
//
//        val uniqueIds = triggerIds.toSet().toList()
//        return triggerRepository.findAllByTriggerIdInAndPremisesId(uniqueIds, premisesActorData.premisesId)
//            .collectList()
//            .flatMap { triggers ->
//                val missingIds = triggers.map { it.triggerId }.toSet() - uniqueIds
//                if (missingIds.isNotEmpty()) {
//                    createMonoError(
//                        BadDataException(
//                            ErrorResponse(
//                                NexoraError.NEXORA0302.errorCode,
//                                "Invalid trigger Ids: ${missingIds.joinToString(",")}"
//                            )
//                        )
//                    )
//                } else {
//                    createMono(triggerIds)
//                }
//            }
//    }
//
//    fun validateConfig(config: TriggerConfig, premisesActorData: PremisesActorData): Mono<TriggerConfig> {
//        val errorCode = NexoraError.NEXORA0307.errorCode
//        return when (config) {
//            is ScheduleTriggerConfig -> validateScheduleTriggerConfig(config, errorCode)
//            is VoiceTriggerConfig -> validateVoiceTriggerConfig(config, errorCode, premisesActorData)
//            is FeedTriggerConfig -> validateFeedTriggerConfig(config)
//        }
//            .map { it as TriggerConfig }
//    }
//
//    private fun validateFeedTriggerConfig(config: FeedTriggerConfig): Mono<FeedTriggerConfig> {
//        return feedGateway.getFeedByFeedId(config.feedId).map { config }
//    }
//
//    private fun validateVoiceTriggerConfig(
//        config: VoiceTriggerConfig,
//        errorCode: String,
//        premisesActorData: PremisesActorData
//    ): Mono<VoiceTriggerConfig> {
//        config.sanitizeCommands()
//        return if (config.commands.isEmpty() || config.commands.any { it.isBlank() }) {
//            createMonoError(
//                BadDataException(
//                    ErrorResponse(errorCode, "Voice commands should not be empty or blank")
//                )
//            )
//        } else {
//            triggerRepository.findAllByPremisesIdAndVoiceCommands(
//                premisesActorData.premisesId,
//                config.commands.map { command ->
//                    Regex("^$command$", RegexOption.IGNORE_CASE)
//                }.joinToString("|")
//            )
//                .flatMap { triggerConverter.toTrigger(it) }
//                .collectList()
//                .map { triggers -> triggers.map { (it.config as VoiceTriggerConfig).commands }.flatten() }
//                .flatMap { commands ->
//                    val conflicts = commands.filter { config.commands.contains(it) }
//                    if (conflicts.isNotEmpty()) {
//                        createMonoError(
//                            BadDataException(
//                                ErrorResponse(errorCode, "Voice command(s) already used: ${conflicts.joinToString()}")
//                            )
//                        )
//                    } else {
//                        createMono(config)
//                    }
//                }
//                .switchIfEmpty(createMono(config))
//        }
//    }
//
//    private fun validateScheduleTriggerConfig(
//        config: ScheduleTriggerConfig,
//        errorCode: String
//    ): Mono<ScheduleTriggerConfig> {
//        return if (config.repeat.isEmpty()) {
//            createMonoError(BadDataException(ErrorResponse(errorCode, "Repeat days are required.")))
//        } else {
//            when (config.config) {
//                is TimeTriggerConfig -> validateTimeTriggerConfig(config.config, errorCode)
//                is SunTriggerConfig -> validateSunTriggerConfig(config.config, errorCode)
//            }
//                .map { config }
//        }
//    }
//
//    private fun validateSunTriggerConfig(config: SunTriggerConfig, errorCode: String): Mono<SunTriggerConfig> {
//        return if (config.offsetMinutes < -60 || config.offsetMinutes > 60) {
//            createMonoError(
//                BadDataException(ErrorResponse(errorCode, "Offset minutes must be between -60 and 60 minutes."))
//            )
//        } else {
//            createMono(config)
//        }
//    }
//
//    private fun validateTimeTriggerConfig(config: TimeTriggerConfig, errorCode: String): Mono<TimeTriggerConfig> {
//        return if (!config.time.matches(Regex("^([01]\\d|2[0-3]):[0-5]\\d$"))) {
//            createMonoError(BadDataException(ErrorResponse(errorCode, "Invalid time format, expected HH:mm")))
//        } else {
//            createMono(config)
//        }
//    }
//}

